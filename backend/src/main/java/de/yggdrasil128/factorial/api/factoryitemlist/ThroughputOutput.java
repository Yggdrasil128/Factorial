package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.engine.QuantityByChangelist;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemOutput;

public class ThroughputOutput {

    private final ItemOutput item;
    private final QuantityByChangelist quantity;

    public ThroughputOutput(Item item, QuantityByChangelist quantity) {
        this.item = new ItemOutput(item);
        this.quantity = quantity;
    }

    public ItemOutput getItem() {
        return item;
    }

    public QuantityByChangelist getQuantity() {
        return quantity;
    }

}
