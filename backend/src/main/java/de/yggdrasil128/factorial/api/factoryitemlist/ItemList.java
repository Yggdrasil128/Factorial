package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.model.QuantityByChangelist;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.transportlink.TransportLink;
import de.yggdrasil128.factorial.model.transportlink.TransportLinkOutput;
import de.yggdrasil128.factorial.model.xgress.Xgress;
import de.yggdrasil128.factorial.model.xgress.XgressOutput;

import java.util.*;

import static java.util.stream.Collectors.toCollection;

public class ItemList {

    private final List<ApiItem> items;
    private final List<ApiProductionStep> productionSteps;
    private final List<TransportLinkOutput> transportLinks = new ArrayList<>();
    private final List<XgressOutput> ingresses;
    private final List<XgressOutput> egresses;

    public ItemList(Factory factory, Changelist primary, Iterable<Changelist> active) {
        // production steps calculate their stuff pretty much themselves
        productionSteps = factory.getProductionSteps().stream()
                .map(productionStep -> new ApiProductionStep(productionStep, primary, active))
                .collect(toCollection(ArrayList::new));
        /*
         * In the following, we go through each and every entity that produces or consumes an item within the given
         * factory. Each time, we calculate the effective production/consumption and reduce the corresponding sum in
         * this mapping's values. Thus, after we finished our work, those values resemble this item list's main entries.
         */
        Map<Item, ApiItem> itemCollector = new HashMap<>();
        for (ApiProductionStep productionStep : productionSteps) {
            for (Throughput input : productionStep.getInput()) {
                recordConsumption(computeItem(itemCollector, input.getItem(), factory), input.getQuantity());
            }
            for (Throughput output : productionStep.getOutput()) {
                recordProduction(computeItem(itemCollector, output.getItem(), factory), output.getQuantity());
            }
        }
        for (TransportLink transportLink : factory.getSave().getTransportLinks()) {
            if (transportLink.getSourceFactory().equals(factory)) {
                for (Resource resource : transportLink.getResources()) {
                    recordConsumption(computeItem(itemCollector, resource.getItem(), factory),
                            QuantityByChangelist.at(resource.getQuantity()));
                    transportLinks.add(new TransportLinkOutput(transportLink));
                }
            } else if (transportLink.getTargetFactory().equals(factory)) {
                for (Resource resource : transportLink.getResources()) {
                    recordProduction(computeItem(itemCollector, resource.getItem(), factory),
                            QuantityByChangelist.at(resource.getQuantity()));
                    transportLinks.add(new TransportLinkOutput(transportLink));
                }
                transportLinks.add(new TransportLinkOutput(transportLink));
            }
        }
        ingresses = new ArrayList<>(factory.getIngresses().size());
        for (Xgress ingress : factory.getIngresses()) {
            for (Resource resource : ingress.getResources()) {
                recordProduction(computeItem(itemCollector, resource.getItem(), factory),
                        QuantityByChangelist.at(resource.getQuantity()));
                ingresses.add(new XgressOutput(ingress));
            }
        }
        egresses = new ArrayList<>(factory.getEgresses().size());
        for (Xgress egress : factory.getEgresses()) {
            for (Resource resource : egress.getResources()) {
                recordConsumption(computeItem(itemCollector, resource.getItem(), factory),
                        QuantityByChangelist.at(resource.getQuantity()));
                egresses.add(new XgressOutput(egress));
            }
        }
        items = new ArrayList<>(itemCollector.values());
        items.sort(Comparator.comparing(ApiItem::getOrdinal));
    }

    private static ApiItem computeItem(Map<Item, ApiItem> itemCollector, Item delegate, Factory factory) {
        return itemCollector.computeIfAbsent(delegate, key -> new ApiItem(key, factory.getItemOrder().get(key)));
    }

    private static void recordConsumption(ApiItem apiItem, QuantityByChangelist consumption) {
        apiItem.getBalances().setConsumption(apiItem.getBalances().getConsumption().add(consumption));
    }

    private static void recordProduction(ApiItem apiItem, QuantityByChangelist production) {
        apiItem.getBalances().setProduction(apiItem.getBalances().getProduction().add(production));
    }

    public List<ApiItem> getItems() {
        return items;
    }

    public List<ApiProductionStep> getProductionSteps() {
        return productionSteps;
    }

    public List<TransportLinkOutput> getTransportLinks() {
        return transportLinks;
    }

    public List<XgressOutput> getIngresses() {
        return ingresses;
    }

    public List<XgressOutput> getEgresses() {
        return egresses;
    }

}
