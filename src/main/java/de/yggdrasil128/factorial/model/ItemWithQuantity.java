package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.FractionConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

@Embeddable
public class ItemWithQuantity {
    @ManyToOne(optional = false)
    private Item item;
    @Convert(converter = FractionConverter.class)
    private Fraction quantity;

    public ItemWithQuantity() {
    }

    public ItemWithQuantity(Item item, Fraction quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Fraction getQuantity() {
        return quantity;
    }

    public void setQuantity(Fraction quantity) {
        this.quantity = quantity;
    }
}
