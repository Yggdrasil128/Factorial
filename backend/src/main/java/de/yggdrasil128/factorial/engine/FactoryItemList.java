package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.transportline.TransportLine;
import de.yggdrasil128.factorial.model.xgress.Xgress;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
                    boolean required = !entry.getKey().getUncloggingInputs().contains(input.getKey());
                    computeBalances(itemBalances, input.getKey()).recordConsumption(input.getValue(), required);
                }
            }
            for (Map.Entry<Item, QuantityByChangelist> output : entry.getValue().getOutput().entrySet()) {
                if (itemFilter.test(output.getKey())) {
                    boolean required = !entry.getKey().getUncloggingOutputs().contains(output.getKey());
                    computeBalances(itemBalances, output.getKey()).recordProduction(output.getValue(), required);
                }
            }
        }
        for (Xgress ingress : factory.getIngresses()) {
            for (Resource resource : ingress.getResources()) {
                if (itemFilter.test(resource.getItem())) {
                    computeBalances(itemBalances, resource.getItem()).recordProduction(
                            QuantityByChangelist.allAt(resource.getQuantity()), !ingress.isUnclogging());
                }
            }
        }
        for (Xgress egress : factory.getEgresses()) {
            for (Resource resource : egress.getResources()) {
                if (itemFilter.test(resource.getItem())) {
                    computeBalances(itemBalances, resource.getItem()).recordConsumption(
                            QuantityByChangelist.allAt(resource.getQuantity()), !egress.isUnclogging());
                }
            }
        }
        Set<TransportLine> ingoingTransportLines = new HashSet<>();
        Set<TransportLine> outgoingTransportLines = new HashSet<>();
        for (TransportLine transportLine : factory.getSave().getTransportLines()) {
            if (transportLine.getSourceFactories().contains(factory)) {
                for (Item item : transportLine.getItems()) {
                    if (itemFilter.test(item)) {
                        Balances balances = itemBalances.get(item);
                        if (null != balances) {
                            balances.setTransportedOut(true);
                            outgoingTransportLines.add(transportLine);
                        }
                    }
                }
            }
            if (transportLine.getTargetFactories().contains(factory)) {
                for (Item item : transportLine.getItems()) {
                    if (itemFilter.test(item)) {
                        Balances balances = itemBalances.get(item);
                        if (null != balances) {
                            balances.setTransportedIn(true);
                            ingoingTransportLines.add(transportLine);
                        }
                    }
                }
            }
        }
        return new FactoryItemList(itemBalances, productionStepTroughputs, ingoingTransportLines,
                outgoingTransportLines);
    }

    private static Balances computeBalances(Map<Item, Balances> itemBalances, Item delegate) {
        return itemBalances.computeIfAbsent(delegate, key -> new Balances());
    }

    private final Map<Item, Balances> itemBalances;
    private final Map<ProductionStep, ProductionStepThroughputs> productionStepTroughputs;
    private final Set<TransportLine> ingoingTransportLines;
    private final Set<TransportLine> outgoingTransportLines;

    public FactoryItemList(Map<Item, Balances> itemBalances,
                           Map<ProductionStep, ProductionStepThroughputs> productionStepTroughputs,
                           Set<TransportLine> ingoingTransportLines, Set<TransportLine> outgoingTransportLines) {
        this.itemBalances = itemBalances;
        this.productionStepTroughputs = productionStepTroughputs;
        this.ingoingTransportLines = ingoingTransportLines;
        this.outgoingTransportLines = outgoingTransportLines;
    }

    public Map<Item, Balances> getItemBalances() {
        return itemBalances;
    }

    public Map<ProductionStep, ProductionStepThroughputs> getProductionStepThroughputs() {
        return productionStepTroughputs;
    }

    public Set<TransportLine> getIngoingTransportLines() {
        return ingoingTransportLines;
    }

    public Set<TransportLine> getOutgoingTransportLines() {
        return outgoingTransportLines;
    }

}
