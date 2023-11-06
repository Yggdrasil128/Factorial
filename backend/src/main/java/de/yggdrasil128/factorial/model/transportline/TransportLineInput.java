package de.yggdrasil128.factorial.model.transportline;

import java.util.List;

public class TransportLineInput {

    private String description;
    private String name;
    private int iconId;
    private List<Integer> sourceFactoryIds;
    private List<Integer> targetFactoryIds;
    private List<Integer> itemIds;

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

    public List<Integer> getSourceFactoryIds() {
        return sourceFactoryIds;
    }

    public void setSourceFactoryIds(List<Integer> sourceFactoryIds) {
        this.sourceFactoryIds = sourceFactoryIds;
    }

    public List<Integer> getTargetFactoryIds() {
        return targetFactoryIds;
    }

    public void setTargetFactoryIds(List<Integer> targetFactoryIds) {
        this.targetFactoryIds = targetFactoryIds;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }

}
