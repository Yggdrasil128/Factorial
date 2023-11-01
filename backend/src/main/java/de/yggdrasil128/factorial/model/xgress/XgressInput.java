package de.yggdrasil128.factorial.model.xgress;

import de.yggdrasil128.factorial.model.resource.ResourceInput;

import java.util.List;

public class XgressInput {

    private String name;
    private Boolean greedy;
    private List<ResourceInput> resources;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isGreedy() {
        return greedy;
    }

    public void setGreedy(Boolean greedy) {
        this.greedy = greedy;
    }

    public List<ResourceInput> getResources() {
        return resources;
    }

    public void setResources(List<ResourceInput> resources) {
        this.resources = resources;
    }

}
