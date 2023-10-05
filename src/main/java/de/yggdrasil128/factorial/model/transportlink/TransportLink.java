package de.yggdrasil128.factorial.model.transportlink;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.resource.Resource;

@Entity
public class TransportLink {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String description;
    @ManyToOne
    private Icon icon;
    @ManyToOne(optional = false)
    private Factory targetFactory;
    @ManyToOne(optional = false)
    private Factory sourceFactory;
    @ElementCollection
    private List<Resource> items = new ArrayList<>();

    public int getId() {
        return id;
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

    public Factory getTargetFactory() {
        return targetFactory;
    }

    public void setTargetFactory(Factory targetFactory) {
        this.targetFactory = targetFactory;
    }

    public Factory getSourceFactory() {
        return sourceFactory;
    }

    public void setSourceFactory(Factory sourceFactory) {
        this.sourceFactory = sourceFactory;
    }

    public List<Resource> getItems() {
        return items;
    }

    public void setItems(List<Resource> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        TransportLink transportLink = (TransportLink) that;

        return id == transportLink.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
