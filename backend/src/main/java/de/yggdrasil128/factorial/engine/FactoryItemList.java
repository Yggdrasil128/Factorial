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
        for (Map.Entry<ProductionStep, ProductionStepThroughputs> entry : productionStepTroughputs.entrySet()) {
            for (Map.Entry<Item, QuantityByChangelist> input : entry.getValue().getInput().entrySet()) {
                boolean greedy = entry.getKey().getInputGreed().contains(input.getKey());
                computeBalances(itemBalances, input.getKey()).recordConsumption(input.getValue(), greedy);
            }
            for (Map.Entry<Item, QuantityByChangelist> output : entry.getValue().getOutput().entrySet()) {
                boolean greedy = entry.getKey().getOutputGreed().contains(output.getKey());
                computeBalances(itemBalances, output.getKey()).recordProduction(output.getValue(), greedy);
            }
        }
        Set<TransportLink> participatingTransportLinks = new HashSet<>();
        for (TransportLink transportLink : factory.getSave().getTransportLinks()) {
            // TODO decide on greediness for transport links
            if (transportLink.getSourceFactory().equals(factory)) {
                for (Resource resource : transportLink.getResources()) {
                    computeBalances(itemBalances, resource.getItem())
                            .recordConsumption(QuantityByChangelist.allAt(resource.getQuantity()), true);
                    participatingTransportLinks.add(transportLink);
                }
            } else if (transportLink.getTargetFactory().equals(factory)) {
                for (Resource resource : transportLink.getResources()) {
                    computeBalances(itemBalances, resource.getItem())
                            .recordProduction(QuantityByChangelist.allAt(resource.getQuantity()), true);
                    participatingTransportLinks.add(transportLink);
                }
            }
        }
        for (Xgress ingress : factory.getIngresses()) {
            for (Resource resource : ingress.getResources()) {
                computeBalances(itemBalances, resource.getItem())
                        .recordProduction(QuantityByChangelist.allAt(resource.getQuantity()), ingress.isGreedy());
            }
        }
        for (Xgress egress : factory.getEgresses()) {
            for (Resource resource : egress.getResources()) {
                computeBalances(itemBalances, resource.getItem())
                        .recordConsumption(QuantityByChangelist.allAt(resource.getQuantity()), egress.isGreedy());
            }
        }
        return new FactoryItemList(itemBalances, productionStepTroughputs, participatingTransportLinks);
    }

    private static Balances computeBalances(Map<Item, Balances> itemBalances, Item delegate) {
        return itemBalances.computeIfAbsent(delegate, key -> new Balances());
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
