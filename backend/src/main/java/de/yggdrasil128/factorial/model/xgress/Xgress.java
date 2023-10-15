package de.yggdrasil128.factorial.model.xgress;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private Factory factory;
    private String name;
    @ElementCollection
    private List<Resource> resources;

    public Xgress() {
    }

    public Xgress(Factory factory, String name, List<Resource> resources) {
        this.factory = factory;
        this.name = name;
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

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

}
