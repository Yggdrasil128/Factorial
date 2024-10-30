package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.FractionConverter;
import de.yggdrasil128.factorial.model.item.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

@Embeddable
public class ItemQuantity {

    @ManyToOne(optional = false)
    private Item item;
    @Column(nullable = false)
    @Convert(converter = FractionConverter.class)
    private Fraction quantity;

    public ItemQuantity() {
    }

    public ItemQuantity(Item item, Fraction quantity) {
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

    @Override
    public String toString() {
        return quantity + " " + item;
    }

}
