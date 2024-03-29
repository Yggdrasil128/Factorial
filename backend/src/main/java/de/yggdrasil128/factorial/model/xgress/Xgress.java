package de.yggdrasil128.factorial.model.xgress;

import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.resource.Resource;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Xgress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    private Factory factory;
    private String name;
    private boolean unclogging;
    @ElementCollection
    private List<Resource> resources;

    public Xgress() {
    }

    public Xgress(Factory factory, String name, boolean unclogging, List<Resource> resources) {
        this.factory = factory;
        this.name = name;
        this.unclogging = unclogging;
        this.resources = resources;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public boolean isUnclogging() {
        return unclogging;
    }

    public void setUnclogging(boolean unclogging) {
        this.unclogging = unclogging;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Xgress && id == ((Xgress) obj).id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

}
