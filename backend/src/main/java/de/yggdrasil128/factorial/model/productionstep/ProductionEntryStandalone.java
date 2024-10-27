package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.QuantityByChangelist;

import java.util.Map;

public class ProductionEntryStandalone {

    private final int itemId;
    private final QuantityByChangelist quantity;

    public ProductionEntryStandalone(Map.Entry<Integer, QuantityByChangelist> entry) {
        itemId = entry.getKey().intValue();
        quantity = entry.getValue();
    }

    public int getItemId() {
        return itemId;
    }

    public QuantityByChangelist getQuantity() {
        return quantity;
    }

}
