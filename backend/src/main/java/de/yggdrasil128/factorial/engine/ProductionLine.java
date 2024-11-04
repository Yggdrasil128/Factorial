package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.ProductionLineService;
import de.yggdrasil128.factorial.model.resource.Resource;
import jakarta.persistence.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages the computation for a <i>production line</i>, which is some {@link Entity} for which we maintain a list of
 * {@link Production Productions}, which we will subsequently call <i>contributors</i>.
 * <p>
 * To be kept up-to-date, this implementation must be notified about changes to contributors, namely
 * <ul>
 * <li>{@link #addContributor(Production)}</li>
 * <li>{@link #updateContribution(Production)} / {@link #updateContributor(Production)}</li>
 * <li>{@link #removeContributor(Production)}</li>
 * </ul>
 * <p>
 * As a result of that, a <i>production line</i> computes a set of <i>resources</i>, represented by
 * {@link ResourceContributions}. These can be set to be {@link ResourceContributions#isImported() imported} or
 * {@link ResourceContributions#isExported() exported}, which in turn provides the basis for calculating the inputs and
 * outputs of the production line. Therefore, a <i>production line</i> is also a {@link Production}.
 */
public class ProductionLine implements Production {

    private final int hostId;
    // key is Item.id, but we must not keep references to the entities here
    private final Map<Integer, ResourceContributions> contributions = new HashMap<>();
    private final ProductionLineService service;
    private boolean hasAlteredResources;

    public ProductionLine(int hostId, ProductionLineService service) {
        this.hostId = hostId;
        this.service = service;
    }

    public ResourceContributions getContributions(Resource resource) {
        return contributions.get(resource.getItem().getId());
    }

    /**
     * A single-use hook for clients to keep track of updates to resources after a method invocation to either
     * {@link #addContributor(Production)} or {@link #removeContributor(Production)} has returned.
     * <p>
     * If that call has <i>altered</i> resources, this method will return {@code true} and reset the corresponding flag.
     * Altering a resource means invoking either {@link ProductionLineService#spawnResource(int, int)} or
     * {@link ProductionLineService#destroyResource(int, int)}.
     * 
     * @return {@code true} if resources were spawned or destroyed; {@code false} otherwise
     */
    public boolean hasAlteredResources() {
        boolean value = hasAlteredResources;
        hasAlteredResources = false;
        return value;
    }

    @Override
    public Map<Integer, QuantityByChangelist> getInputs() {
        return contributions.values().stream().filter(ResourceContributions::isImported)
                .collect(Collectors.toMap(ResourceContributions::getItemId, ResourceContributions::getOverConsumed));
    }

    @Override
    public QuantityByChangelist getInput(int itemId) {
        ResourceContributions contribution = contributions.get(itemId);
        return null != contribution && contribution.isImported() ? contribution.getOverConsumed()
                : QuantityByChangelist.ZERO;
    }

    @Override
    public Map<Integer, QuantityByChangelist> getOutputs() {
        return contributions.values().stream().filter(ResourceContributions::isExported)
                .collect(Collectors.toMap(ResourceContributions::getItemId, ResourceContributions::getOverProduced));
    }

    @Override
    public QuantityByChangelist getOutput(int itemId) {
        ResourceContributions contribution = contributions.get(itemId);
        return null != contribution && contribution.isExported() ? contribution.getOverProduced()
                : QuantityByChangelist.ZERO;
    }

    /**
     * Manually adds a {@link Resource} for the production line. Has no effect if the resource is already being tracked.
     * <p>
     * This method is meant primarily for populating the resources of a production line before any contributors were
     * added (which is optional). Also, this is (currently) required for resources that have no contributors but are
     * simply for importing/exporting items to/from a factory.
     * 
     * @param resource the {@link Resource} for which to track contributions
     */
    public void addResource(Resource resource) {
        /*
         * The 'ResourceContributions' constructor is (in contrast to most other engine-objects) light-weight, so this
         * has no side-effects, if the 'Resource' is already present in 'contributions'.
         */
        contributions.putIfAbsent(resource.getItem().getId(), new ResourceContributions(resource));
    }

    public void updateResource(Resource resource) {
        contributions.computeIfAbsent(resource.getItem().getId(), key -> new ResourceContributions(resource))
                .update(resource);
    }

    /**
     * Notifies of an addition of a contributor. This generally necessitates a recalculation of contributions.
     * 
     * @param contributor the contributor that was added
     */
    public void addContributor(Production contributor) {
        HashMap<Integer, ResourceContributions> modified = new HashMap<>();
        initContribution(contributor, modified);
        for (ResourceContributions contribution : modified.values()) {
            service.notifyResourceUpdate(hostId, contribution);
        }
    }

    /**
     * Notifies of an update on a contributor that necessitates a recalculation of its contributions.
     * <p>
     * This implementation assumes that the provided object
     * <ul>
     * <li>reflects the updated state</li>
     * <li>is the exact contributor that is currently known to this production line</li>
     * </ul>
     * 
     * @param contributor the contributor that was updated
     */
    public void updateContribution(Production contributor) {
        Map<Integer, ResourceContributions> modified = new HashMap<>();
        /**
         * The contributor reflects the updated state, so we cannot select the contributions that are currently still
         * referencing it (like we do in #removeContributor). Instead, we must check them all.
         */
        for (ResourceContributions contribution : contributions.values()) {
            if (contribution.getProducers().remove(contributor) | contribution.getConsumers().remove(contributor)) {
                modified.put(contribution.getItemId(), contribution);
            }
        }
        initContribution(contributor, modified);
        updateOrDestroyContributions(modified);
    }

    private void initContribution(Production contributor, Map<Integer, ResourceContributions> modified) {
        /*
         * There may be contributors that have the same item as both input and output, but still we want to invoke the
         * callbacks only once per resource. Thus, we must collect these by item and process them in a separate loop.
         */
        for (Integer input : contributor.getInputs().keySet()) {
            ResourceContributions contribution = contributions.computeIfAbsent(input, this::spawnResource);
            modified.put(input, contribution);
            contribution.getConsumers().add(contributor);
        }
        for (Integer output : contributor.getOutputs().keySet()) {
            ResourceContributions contribution = contributions.computeIfAbsent(output, this::spawnResource);
            modified.put(output, contribution);
            contribution.getProducers().add(contributor);
        }
    }

    private ResourceContributions spawnResource(int itemId) {
        hasAlteredResources = true;
        return service.spawnResource(hostId, itemId);
    }

    /**
     * Notifies of an update on a contributor that does not necessitate a recalculation of its contributions.
     * <p>
     * This implementation assumes that the provided object
     * <ul>
     * <li>reflects the updated state</li>
     * <li>is the exact contributor that is currently known to this production line</li>
     * </ul>
     * 
     * @param contributor the contributor that was updated
     */
    public void updateContributor(Production contributor) {
        Set<Integer> affected = new HashSet<>();
        affected.addAll(contributor.getInputs().keySet());
        affected.addAll(contributor.getOutputs().keySet());
        for (Integer itemId : affected) {
            service.notifyResourceUpdate(hostId, contributions.get(itemId));
        }
    }

    /**
     * Notifies of a removal of a contributor. This generally necessitates a recalculation of contributions.
     * 
     * @param contributor the contributor that was removed
     */
    public void removeContributor(Production contributor) {
        /*
         * There may be contributors that have the same item as both input and output, but still we want to invoke the
         * callbacks only once per resource. Thus, we must collect these by item and process them in a separate loop.
         */
        Map<Integer, ResourceContributions> modified = new HashMap<>();
        for (Integer input : contributor.getInputs().keySet()) {
            ResourceContributions contribution = contributions.get(input);
            if (contribution.getConsumers().remove(contributor)) {
                modified.put(input, contribution);
            }
        }
        for (Integer output : contributor.getOutputs().keySet()) {
            ResourceContributions contribution = contributions.get(output);
            if (contribution.getProducers().remove(contributor)) {
                modified.put(output, contribution);
            }
        }
        updateOrDestroyContributions(modified);
    }

    private void updateOrDestroyContributions(Map<Integer, ResourceContributions> modified) {
        for (Map.Entry<Integer, ResourceContributions> entry : modified.entrySet()) {
            ResourceContributions contribution = entry.getValue();
            if (canDestroy(contribution)) {
                contributions.remove(entry.getKey());
                service.destroyResource(hostId, contribution.getResourceId());
                hasAlteredResources = true;
            } else {
                service.notifyResourceUpdate(hostId, contribution);
            }
        }
    }

    private static boolean canDestroy(ResourceContributions contribution) {
        return !contribution.isImported() && !contribution.isExported() && contribution.getProducers().isEmpty()
                && contribution.getConsumers().isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProductionLine && hostId == ((ProductionLine) obj).hostId;
    }

    @Override
    public int hashCode() {
        return 31 + Integer.hashCode(hostId);
    }

    @Override
    public String toString() {
        return contributions.values().toString();
    }

}
