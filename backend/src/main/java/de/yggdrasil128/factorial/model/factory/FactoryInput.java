package de.yggdrasil128.factorial.model.factory;

import java.util.List;

public class FactoryInput {

    private int ordinal;
    private String name;
    private String description;
    private int iconId;
    private List<Integer> itemOrder;

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
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

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public List<Integer> getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(List<Integer> itemOrder) {
        this.itemOrder = itemOrder;
    }

}
