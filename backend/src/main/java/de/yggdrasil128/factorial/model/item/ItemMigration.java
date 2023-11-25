package de.yggdrasil128.factorial.model.item;

import java.util.List;

import static java.util.Collections.emptyList;

public class ItemMigration {

    private String description;
    private String iconName;
    private List<String> category;

    public ItemMigration() {
        category = emptyList();
    }

    public ItemMigration(String description, String iconName, List<String> category) {
        this.description = description;
        this.iconName = iconName;
        this.category = category;
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

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
