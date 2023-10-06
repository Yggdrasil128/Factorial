package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.model.Fraction;

public class ResourceMigration {

    private String itemName;
    private Fraction quantity;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Fraction getQuantity() {
        return quantity;
    }

    public void setQuantity(Fraction quantity) {
        this.quantity = quantity;
    }

}
