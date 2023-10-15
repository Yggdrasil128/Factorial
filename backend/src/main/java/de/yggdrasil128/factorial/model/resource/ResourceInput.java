package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.model.Fraction;

public class ResourceInput {

    private int itemId;
    private Fraction quantity;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Fraction getQuantity() {
        return quantity;
    }

    public void setQuantity(Fraction quantity) {
        this.quantity = quantity;
    }

}
