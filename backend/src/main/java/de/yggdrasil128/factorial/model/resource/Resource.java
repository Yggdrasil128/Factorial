package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;
import jakarta.persistence.*;

@Entity
public class Resource {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne(optional = false)
    private Factory factory;
    @Column(nullable = false)
    private int ordinal;
    @ManyToOne(optional = false)
    private Item item;
    @Column(nullable = false)
    private boolean imported;
    @Column(nullable = false)
    private boolean exported;

    public Resource() {
    }

    public Resource(Factory factory, ResourceStandalone standalone) {
        this.factory = factory;
        applyBasics(standalone);
    }

    public void applyBasics(ResourceStandalone standalone) {
        OptionalInputField.of(standalone.ordinal()).apply(this::setOrdinal);
        OptionalInputField.of(standalone.imported()).apply(this::setImported);
        OptionalInputField.of(standalone.exported()).apply(this::setExported);
    }

    public int getId() {
        return id;
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    @Override
    public String toString() {
        return item + ", imported=" + imported + ", exported=" + exported;
    }

}
