package de.yggdrasil128.factorial.api.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

public class ItemList {

    private final List<ApiItem> items;
    private final List<ApiProductionStep> productionSteps;

    public ItemList(Collection<ProductionStep> productionSteps, Changelist primary, Iterable<Changelist> active) {
        this.productionSteps = productionSteps.stream()
            .map(productionStep -> new ApiProductionStep(productionStep, primary, active)).toList();
        Map<Item, ApiItem> itemCache = new HashMap<>();
        for (ApiProductionStep productionStep : this.productionSteps) {
            for (Throughput input : productionStep.getInput()) {
                ApiItem apiItem = itemCache.computeIfAbsent(input.getItem(), ApiItem::new);
                apiItem.getBalances().setConsumption(apiItem.getBalances().getConsumption().add(input.getQuantity()));
            }
            for (Throughput output : productionStep.getOutput()) {
                ApiItem apiItem = itemCache.computeIfAbsent(output.getItem(), ApiItem::new);
                apiItem.getBalances().setProduction(apiItem.getBalances().getProduction().add(output.getQuantity()));
            }
        }
        items = new ArrayList<>(itemCache.values());
        // TODO sort items by ordinal
    }

    public List<ApiItem> getItems() {
        return items;
    }

    public List<ApiProductionStep> getProductionSteps() {
        return productionSteps;
    }

}
