package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;

import java.util.*;

import static java.util.stream.Collectors.toCollection;

public class ItemList {

    private final List<ApiItem> items;
    private final List<ApiProductionStep> productionSteps;

    public ItemList(Factory factory, Changelist primary, Iterable<Changelist> active) {
        productionSteps = factory.getProductionSteps().stream()
                .map(productionStep -> new ApiProductionStep(productionStep, primary, active))
                .collect(toCollection(ArrayList::new));
        Map<Item, ApiItem> itemCache = new HashMap<>();
        for (ApiProductionStep productionStep : productionSteps) {
            for (Throughput input : productionStep.getInput()) {
                ApiItem apiItem = itemCache.computeIfAbsent(input.getItem(),
                        key -> new ApiItem(key, factory.getItemOrder().get(key)));
                apiItem.getBalances().setConsumption(apiItem.getBalances().getConsumption().add(input.getQuantity()));
            }
            for (Throughput output : productionStep.getOutput()) {
                ApiItem apiItem = itemCache.computeIfAbsent(output.getItem(),
                        key -> new ApiItem(key, factory.getItemOrder().get(key)));
                apiItem.getBalances().setProduction(apiItem.getBalances().getProduction().add(output.getQuantity()));
            }
        }
        items = new ArrayList<>(itemCache.values());
        items.sort(Comparator.comparing(ApiItem::getOrdinal));
    }

    public List<ApiItem> getItems() {
        return items;
    }

    public List<ApiProductionStep> getProductionSteps() {
        return productionSteps;
    }

}
