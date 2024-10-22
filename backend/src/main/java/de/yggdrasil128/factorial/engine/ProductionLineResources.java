package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.resource.Resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class ProductionLineResources {

    // key is Item.id, but we must not keep references to the entities here
    private final Map<Integer, ResourceContributions> contributions = new HashMap<>();
    private final IntFunction<? extends ResourceContributions> resourceFactory;
    private final BiConsumer<? super Integer, ? super ResourceContributions> resourceNotifier;
    private final IntConsumer resourceFinalizer;

    public ProductionLineResources(IntFunction<? extends ResourceContributions> resourceFactory,
                                   BiConsumer<? super Integer, ? super ResourceContributions> resourceNotifier,
                                   IntConsumer resourceFinalizer) {
        this.resourceFactory = resourceFactory;
        this.resourceNotifier = resourceNotifier;
        this.resourceFinalizer = resourceFinalizer;
    }

    public Map<Integer, ResourceContributions> getContributions() {
        return contributions;
    }

    public void addResource(Resource resource) {
        contributions.put(resource.getItem().getId(), new ResourceContributions(resource));
    }

    public void addContributor(Production contributor) {
        /*
         * There may be contributors that have the same item as both input and output, but still we want to invoke the
         * callbacks only once per resource. Thus, we must collect these by item and process them in a separate loop.
         */
        Map<Integer, ResourceContributions> modified = new HashMap<>();
        for (Integer input : contributor.getInputs().keySet()) {
            ResourceContributions contribution = contributions.compute(input,
                    (itemId, previous) -> computeIfAbsent(modified, itemId, previous));
            if (contribution.getConsumers().add(contributor)) {
                modified.put(input, contribution);
            }
        }
        for (Integer output : contributor.getOutputs().keySet()) {
            ResourceContributions contribution = contributions.compute(output,
                    (itemId, previous) -> computeIfAbsent(modified, itemId, previous));
            if (contribution.getProducers().add(contributor)) {
                modified.put(output, contribution);
            }
        }
        for (Map.Entry<Integer, ResourceContributions> entry : modified.entrySet()) {
            resourceNotifier.accept(entry.getKey(), entry.getValue());
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

    public void updateContributor(Production contributor) {
        /*
         * We don't need to invalidate anything locally, because the ProductionStep updates its throughputs without
         * invalidating the throughput objects, therefore our resources are updated implicitly as well.
         */
        Set<Integer> affected = new HashSet<>();
        affected.addAll(contributor.getInputs().keySet());
        affected.addAll(contributor.getOutputs().keySet());
        for (Integer itemId : affected) {
            resourceNotifier.accept(itemId, contributions.get(itemId));
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
                resourceNotifier.accept(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public String toString() {
        return contributions.values().toString();
    }

}
