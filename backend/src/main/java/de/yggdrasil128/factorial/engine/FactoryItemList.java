package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.transportlink.TransportLink;
import de.yggdrasil128.factorial.model.xgress.Xgress;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class FactoryItemList {

    public static FactoryItemList of(Factory factory) {
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
        for (ProductionStepThroughputs productionStep : productionStepTroughputs.values()) {
            for (Map.Entry<Item, QuantityByChangelist> input : productionStep.getInput().entrySet()) {
                recordConsumption(computeBalances(itemBalances, input.getKey()), input.getValue());
            }
            for (Map.Entry<Item, QuantityByChangelist> output : productionStep.getOutput().entrySet()) {
                recordProduction(computeBalances(itemBalances, output.getKey()), output.getValue());
            }
        }
        Set<TransportLink> participatingTransportLinks = new HashSet<>();
        for (TransportLink transportLink : factory.getSave().getTransportLinks()) {
            // TODO prohibit a transport link leading from a factory to itself
            if (transportLink.getSourceFactory().equals(factory)) {
                for (Resource resource : transportLink.getResources()) {
                    recordConsumption(computeBalances(itemBalances, resource.getItem()),
                            QuantityByChangelist.allAt(resource.getQuantity()));
                    participatingTransportLinks.add(transportLink);
                }
            } else if (transportLink.getTargetFactory().equals(factory)) {
                for (Resource resource : transportLink.getResources()) {
                    recordProduction(computeBalances(itemBalances, resource.getItem()),
                            QuantityByChangelist.allAt(resource.getQuantity()));
                    participatingTransportLinks.add(transportLink);
                }
            }
        }
        for (Xgress ingress : factory.getIngresses()) {
            for (Resource resource : ingress.getResources()) {
                recordProduction(computeBalances(itemBalances, resource.getItem()),
                        QuantityByChangelist.allAt(resource.getQuantity()));
            }
        }
        for (Xgress egress : factory.getEgresses()) {
            for (Resource resource : egress.getResources()) {
                recordConsumption(computeBalances(itemBalances, resource.getItem()),
                        QuantityByChangelist.allAt(resource.getQuantity()));
            }
        }
        return new FactoryItemList(itemBalances, productionStepTroughputs, participatingTransportLinks);
    }

    private static Balances computeBalances(Map<Item, Balances> itemBalances, Item delegate) {
        return itemBalances.computeIfAbsent(delegate, key -> new Balances());
    }

    private static void recordConsumption(Balances balances, QuantityByChangelist consumption) {
        balances.setConsumption(balances.getConsumption().add(consumption));
    }

    private static void recordProduction(Balances balances, QuantityByChangelist production) {
        balances.setProduction(balances.getProduction().add(production));
    }

    private Map<Item, Balances> itemBalances = new HashMap<>();
    private Map<ProductionStep, ProductionStepThroughputs> productionStepTroughputs = new HashMap<>();
    private Set<TransportLink> participatingTransportLinks = new HashSet<>();

    public FactoryItemList(Map<Item, Balances> itemBalances,
                           Map<ProductionStep, ProductionStepThroughputs> productionStepTroughputs,
                           Set<TransportLink> participatingTransportLinks) {
        this.itemBalances = itemBalances;
        this.productionStepTroughputs = productionStepTroughputs;
        this.participatingTransportLinks = participatingTransportLinks;
    }

    public Map<Item, Balances> getItemBalances() {
        return itemBalances;
    }

    public Map<ProductionStep, ProductionStepThroughputs> getProductionStepThroughputs() {
        return productionStepTroughputs;
    }

    public Set<TransportLink> getParticipatingTransportLinks() {
        return participatingTransportLinks;
    }

}
