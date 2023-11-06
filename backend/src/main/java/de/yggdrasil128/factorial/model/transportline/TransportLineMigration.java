package de.yggdrasil128.factorial.model.transportline;

import java.util.List;

public class TransportLineMigration {

    private String name;
    private String description;
    private String iconName;
    private List<String> sourceFactoryNames;
    private List<String> targetFactoryNames;
    private List<String> itemNames;

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

    public List<String> getSourceFactoryNames() {
        return sourceFactoryNames;
    }

    public void setSourceFactoryNames(List<String> sourceFactoryNames) {
        this.sourceFactoryNames = sourceFactoryNames;
    }

    public List<String> getTargetFactoryNames() {
        return targetFactoryNames;
    }

    public void setTargetFactoryNames(List<String> targetFactoryNames) {
        this.targetFactoryNames = targetFactoryNames;
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }

}
