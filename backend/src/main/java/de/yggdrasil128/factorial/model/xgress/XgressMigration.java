package de.yggdrasil128.factorial.model.xgress;

import de.yggdrasil128.factorial.model.Fraction;

import java.util.Map;

public class XgressMigration {

    private String name;
    private boolean unclogging;
    private Map<String, Fraction> resources;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnclogging() {
        return unclogging;
    }

    public void setUnclogging(boolean unclogging) {
        this.unclogging = unclogging;
    }

    public Map<String, Fraction> getResources() {
        return resources;
    }

    public void setResources(Map<String, Fraction> resources) {
        this.resources = resources;
    }

}
