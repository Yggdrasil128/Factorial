package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.model.OrderedModel;
import de.yggdrasil128.factorial.model.item.Item;
import jakarta.persistence.*;

@MappedSuperclass
public class Resource implements OrderedModel {

    @Id
    @GeneratedValue
    private int id;
    @Column(nullable = false)
    private int ordinal;
    @ManyToOne(optional = false)
    private Item item;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return item.toString();
    }

}
