package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.engine.FactoryItemList;
import de.yggdrasil128.factorial.model.factory.Factory;

import java.util.Comparator;
import java.util.List;

public class ItemListOutput {

    private final List<ListItemOutput> items;
    private final List<ListProductionStepOutput> productionSteps;

    public ItemListOutput(Factory factory, FactoryItemList delegate) {
        items = delegate.getItemBalances().entrySet().stream()
                .map(entry -> new ListItemOutput(entry.getKey(),
                        factory.getItemOrder().getOrDefault(entry.getKey(), Integer.MAX_VALUE), entry.getValue()))
                .sorted(Comparator.comparing(ListItemOutput::getOrdinal)).toList();
        productionSteps = delegate.getProductionStepThroughputs().entrySet().stream()
                .map(entry -> new ListProductionStepOutput(entry.getKey(), entry.getValue())).toList();
    }

    public List<ListItemOutput> getItems() {
        return items;
    }

    public List<ListProductionStepOutput> getProductionSteps() {
        return productionSteps;
    }

}
