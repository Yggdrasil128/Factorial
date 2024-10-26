package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.resource.Resource;
import jakarta.persistence.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/**
 * Manages the {@link ResourceContributions} in a <i>production line</i>, which is some {@link Entity} for which we
 * maintain a list of {@link Production Productions}, which we call <i>contributors</i>.
 * <p>
 * To be kept up-to-date, this implementation must be notified about changes to contributors, namely
 * <ul>
 * <li>{@link #addContributor(Production)}</li>
 * <li>{@link #updateContributor(Production)}</li>
 * <li>{@link #removeContributor(Production)}</li>
 * </ul>
 */
public class ProductionLineResources {

    // key is Item.id, but we must not keep references to the entities here
    private final Map<Integer, ResourceContributions> contributions = new HashMap<>();
    private final IntFunction<? extends ResourceContributions> resourceFactory;
    private final Consumer<? super ResourceContributions> resourceNotifier;
    private final IntConsumer resourceFinalizer;

    public ProductionLineResources(IntFunction<? extends ResourceContributions> resourceFactory,
                                   Consumer<? super ResourceContributions> resourceNotifier,
                                   IntConsumer resourceFinalizer) {
        this.resourceFactory = resourceFactory;
        this.resourceNotifier = resourceNotifier;
        this.resourceFinalizer = resourceFinalizer;
    }

    public ResourceContributions getContributions(Resource resource) {
        return contributions.get(resource.getItem().getId());
    }

    /**
     * Manually adds a {@link Resource} for the production line.
     * <p>
     * This method is meant primarily for populating the resources of a production line before any contributors were
     * added (which is optional). Also, this is (currently) required for resources that have no contributors but are
     * simply for importing/exporting items to/from a factory.
     * 
     * @param resource the {@link Resource} for which to track contributions
     */
    public void addResource(Resource resource) {
        contributions.putIfAbsent(resource.getItem().getId(), new ResourceContributions(resource));
    }

    public void addContributor(Production contributor) {
        /*
         * There may be contributors that have the same item as both input and output, but still we want to invoke the
         * callbacks only once per resource. Thus, we must collect these by item and process them in a separate loop.
         */
        Map<Integer, ResourceContributions> modified = new HashMap<>();
        for (Integer input : contributor.getInputs().keySet()) {
            contributions.compute(input, (itemId, previous) -> computeIfAbsent(modified, itemId, previous))
                    .getConsumers().add(contributor);
        }
        for (Integer output : contributor.getOutputs().keySet()) {
            contributions.compute(output, (itemId, previous) -> computeIfAbsent(modified, itemId, previous))
                    .getProducers().add(contributor);
        }
        for (ResourceContributions contribution : modified.values()) {
            resourceNotifier.accept(contribution);
        }
    }

    private ResourceContributions computeIfAbsent(Map<Integer, ResourceContributions> modified, int itemId,
                                                  ResourceContributions previous) {
        if (null == previous) {
            return resourceFactory.apply(itemId);
        }
        modified.put(itemId, previous);
        return previous;
    }

    /**
     * Notifies of an update on a contributor.
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
        /*
         * We don't need to invalidate anything locally, because the ProductionStep updates its throughputs without
         * invalidating the throughput objects, therefore our resources are updated implicitly as well.
         */
        Set<Integer> affected = new HashSet<>();
        affected.addAll(contributor.getInputs().keySet());
        affected.addAll(contributor.getOutputs().keySet());
        for (Integer itemId : affected) {
            resourceNotifier.accept(contributions.get(itemId));
        }
    }

    public void removeContributor(Production contributor) {
        /*
         * There may be contributors that have the same item as both input and output, but still we want to invoke the
         * callbacks only once per resource. Thus, we must collect these by item and process them in a separate loop.
         */
        Map<Integer, ResourceContributions> modified = new HashMap<>();
        for (Integer input : contributor.getInputs().keySet()) {
            ResourceContributions contribution = contributions.get(input);
            if (contribution.getProducers().remove(contributor)) {
                modified.put(input, contribution);
            }
        }
        for (Integer output : contributor.getOutputs().keySet()) {
            ResourceContributions contribution = contributions.get(output);
            if (contribution.getConsumers().remove(contributor)) {
                modified.put(output, contribution);
            }
        }
        for (Map.Entry<Integer, ResourceContributions> entry : modified.entrySet()) {
            if (entry.getValue().getProducers().isEmpty() && entry.getValue().getConsumers().isEmpty()) {
                contributions.remove(entry.getKey());
                resourceFinalizer.accept(entry.getKey());
            } else {
                resourceNotifier.accept(entry.getValue());
            }
        }
    }

    @Override
    public String toString() {
        return contributions.values().toString();
    }

}
