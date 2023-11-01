package de.yggdrasil128.factorial.model.xgress;

import de.yggdrasil128.factorial.model.Fraction;

import java.util.Map;

public class XgressMigration {

    private String name;
    private boolean greedy;
    private Map<String, Fraction> resources;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGreedy() {
        return greedy;
    }

    public void setGreedy(boolean greedy) {
        this.greedy = greedy;
    }

    public Map<String, Fraction> getResources() {
        return resources;
    }

    public void setResources(Map<String, Fraction> resources) {
        this.resources = resources;
    }

}
