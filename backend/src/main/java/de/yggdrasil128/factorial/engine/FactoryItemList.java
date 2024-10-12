package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;

public class FactoryItemList {

    public static FactoryItemList of(Factory factory) {
        return of(factory, item -> true);
    }

    public static FactoryItemList of(Factory factory, Predicate<Item> itemFilter) {
        Changelists changelists = Changelists.of(factory.getSave());
        Map<ProductionStep, ProductionStepThroughputs> productionStepTroughputs = factory.getProductionSteps().stream()
                .collect(toMap(Function.identity(),
                        productionStep -> ProductionStepThroughputs.of(productionStep, changelists)));
        Map<Item, Balances> itemBalances = new HashMap<>();
        /*
         * In the following, we go through each and every entity that produces or consumes an item within the given
         * factory. Each time, we calculate the effective production/consumption and reduce the corresponding sum in
         * this mapping's values. Thus, after we finished our work, those values resemble this item list's main entries.
         */
        for (Map.Entry<ProductionStep, ProductionStepThroughputs> entry : productionStepTroughputs.entrySet()) {
            for (Map.Entry<Item, QuantityByChangelist> input : entry.getValue().getInput().entrySet()) {
                if (itemFilter.test(input.getKey())) {
                    computeBalances(itemBalances, input.getKey()).recordConsumption(input.getValue(), true);
                }
            }
            for (Map.Entry<Item, QuantityByChangelist> output : entry.getValue().getOutput().entrySet()) {
                if (itemFilter.test(output.getKey())) {
                    computeBalances(itemBalances, output.getKey()).recordProduction(output.getValue(), true);
                }
            }
        }
        return new FactoryItemList(itemBalances, productionStepTroughputs);
    }

    private static Balances computeBalances(Map<Item, Balances> itemBalances, Item delegate) {
        return itemBalances.computeIfAbsent(delegate, key -> new Balances());
    }

    private final Map<Item, Balances> itemBalances;
    private final Map<ProductionStep, ProductionStepThroughputs> productionStepTroughputs;

    public FactoryItemList(Map<Item, Balances> itemBalances,
                           Map<ProductionStep, ProductionStepThroughputs> productionStepTroughputs) {
        this.itemBalances = itemBalances;
        this.productionStepTroughputs = productionStepTroughputs;
    }

    public Map<Item, Balances> getItemBalances() {
        return itemBalances;
    }

    public Map<ProductionStep, ProductionStepThroughputs> getProductionStepThroughputs() {
        return productionStepTroughputs;
    }

}
