package de.yggdrasil128.factorial.api.factoryitemlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.yggdrasil128.factorial.model.QuantityByChangelist;
import de.yggdrasil128.factorial.model.item.Item;

public class Throughput {

    private final Item item;
    private final QuantityByChangelist quantity;

    public Throughput(Item item, QuantityByChangelist quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    @JsonIgnore
    public Item getItem() {
        return item;
    }

    public int getItemId() {
        return item.getId();
    }

    public QuantityByChangelist getQuantity() {
        return quantity;
    }

}
