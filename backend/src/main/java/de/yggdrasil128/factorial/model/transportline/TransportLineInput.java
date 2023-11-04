package de.yggdrasil128.factorial.model.transportline;

import de.yggdrasil128.factorial.model.resource.ResourceInput;

import java.util.List;

public class TransportLineInput {

    private String description;
    private String name;
    private int iconId;
    private int sourceFactoryId;
    private int targetFactoryId;
    private List<ResourceInput> resources;

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

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getSourceFactoryId() {
        return sourceFactoryId;
    }

    public void setSourceFactoryId(int sourceFactoryId) {
        this.sourceFactoryId = sourceFactoryId;
    }

    public int getTargetFactoryId() {
        return targetFactoryId;
    }

    public void setTargetFactoryId(int targetFactoryId) {
        this.targetFactoryId = targetFactoryId;
    }

    public List<ResourceInput> getResources() {
        return resources;
    }

    public void setResources(List<ResourceInput> resources) {
        this.resources = resources;
    }

}
