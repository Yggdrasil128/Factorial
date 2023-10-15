package de.yggdrasil128.factorial.model.transportlink;

import de.yggdrasil128.factorial.model.Fraction;

import java.util.Map;

public class TransportLinkMigration {

    private String name;
    private String description;
    private String iconName;
    private String sourceFactoryName;
    private String targetFactoryName;
    private Map<String, Fraction> resources;

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

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getSourceFactoryName() {
        return sourceFactoryName;
    }

    public void setSourceFactoryName(String sourceFactoryName) {
        this.sourceFactoryName = sourceFactoryName;
    }

    public String getTargetFactoryName() {
        return targetFactoryName;
    }

    public void setTargetFactoryName(String targetFactoryName) {
        this.targetFactoryName = targetFactoryName;
    }

    public Map<String, Fraction> getResources() {
        return resources;
    }

    public void setResources(Map<String, Fraction> resources) {
        this.resources = resources;
    }

}
