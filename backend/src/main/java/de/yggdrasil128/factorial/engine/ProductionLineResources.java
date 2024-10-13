package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionLineResources {

    public static List<Resource> of(List<? extends Production> contributors) {
        Map<Item, Resource> cache = new HashMap<>();
        for (Production contributor : contributors) {
            for (Map.Entry<Item, QuantityByChangelist> input : contributor.getInputs().entrySet()) {
                cache.computeIfAbsent(input.getKey(), Resource::new).getConsumers().add(contributor);
            }
            for (Map.Entry<Item, QuantityByChangelist> output : contributor.getOutputs().entrySet()) {
                cache.computeIfAbsent(output.getKey(), Resource::new).getProducers().add(contributor);
            }
        }
        return new ArrayList<>(cache.values());
    }

}
