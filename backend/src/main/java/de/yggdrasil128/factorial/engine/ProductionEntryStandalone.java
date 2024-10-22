package de.yggdrasil128.factorial.engine;

import java.util.Map;

public class ProductionEntryStandalone {

    private final int item;
    private final QuantityByChangelist quantity;

    public ProductionEntryStandalone(Map.Entry<Integer, QuantityByChangelist> entry) {
        item = entry.getKey().intValue();
        quantity = entry.getValue();
    }

    public int getItem() {
        return item;
    }

    public QuantityByChangelist getQuantity() {
        return quantity;
    }

}
