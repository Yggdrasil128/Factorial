package de.yggdrasil128.factorial.model.transportline;

import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class TransportLine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    private Save save;
    private String name;
    private String description;
    @ManyToOne
    private Icon icon;
    @ManyToMany
    private List<Factory> sourceFactories;
    @ManyToMany
    private List<Factory> targetFactories;
    @ManyToMany
    private List<Item> items;

    public TransportLine() {
    }

    public TransportLine(Save save, String name, String description, Icon icon, List<Factory> sourceFactories,
                         List<Factory> targetFactories, List<Item> items) {
        this.save = save;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sourceFactories = sourceFactories;
        this.targetFactories = targetFactories;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public Save getSave() {
        return save;
    }

    public void setSave(Save save) {
        this.save = save;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public List<Factory> getSourceFactories() {
        return sourceFactories;
    }

    public void setSourceFactories(List<Factory> sourceFactories) {
        this.sourceFactories = sourceFactories;
    }

    public List<Factory> getTargetFactories() {
        return targetFactories;
    }

    public void setTargetFactories(List<Factory> targetFactories) {
        this.targetFactories = targetFactories;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        TransportLine transportLine = (TransportLine) that;

        return id == transportLine.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name + " transporting " + items + " from " + sourceFactories + " to " + targetFactories;
    }

}
