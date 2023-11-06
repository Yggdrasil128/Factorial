package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.transportline.TransportLine;

import java.util.*;

public class TransportLineUsage {

    public static Map<Item, Map<Factory, TransportLineUsage>> of(TransportLine transportLine) {
        // preserve the order of items within the transport line 
        Map<Item, Map<Factory, TransportLineUsage>> usages = new LinkedHashMap<>();
        // we use this for the various times we will invoke #contains(Item)
        Set<Item> itemCache = new HashSet<>(transportLine.getItems());
        for (Factory factory : transportLine.getSourceFactories()) {
            FactoryItemList itemList = FactoryItemList.of(factory, itemCache::contains);
            for (Item item : transportLine.getItems()) {
                Balances balances = itemList.getItemBalances().get(item);
                if (null != balances) {
                    computeUsage(usages, factory, item).addSupply(balances.getProductionAvailable());
                }
            }
        }
        for (Factory factory : transportLine.getTargetFactories()) {
            FactoryItemList itemList = FactoryItemList.of(factory, itemCache::contains);
            for (Item item : transportLine.getItems()) {
                Balances balances = itemList.getItemBalances().get(item);
                if (null != balances) {
                    computeUsage(usages, factory, item).addDemand(balances.getProductionAvailable());
                }
            }
        }
        return usages;
    }

    private static TransportLineUsage computeUsage(Map<Item, Map<Factory, TransportLineUsage>> usages, Factory factory,
                                                   Item item) {
        return usages.computeIfAbsent(item, key -> new HashMap<>()).computeIfAbsent(factory,
                key -> new TransportLineUsage());
    }

    private QuantityByChangelist supply = QuantityByChangelist.allAt(Fraction.ZERO);
    private QuantityByChangelist demand = QuantityByChangelist.allAt(Fraction.ZERO);

    public TransportLineUsage() {
    }

    public QuantityByChangelist getSupply() {
        return supply;
    }

    private void addSupply(QuantityByChangelist production) {
        supply = supply.add(production);
    }

    public QuantityByChangelist getDemand() {
        return demand;
    }

    private void addDemand(QuantityByChangelist consumption) {
        demand = demand.add(consumption);
    }

}
