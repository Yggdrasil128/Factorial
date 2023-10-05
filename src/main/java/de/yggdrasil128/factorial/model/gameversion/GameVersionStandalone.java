package de.yggdrasil128.factorial.model.gameversion;

import static java.util.Collections.emptyList;

import java.util.List;

public class GameVersionStandalone {

    private String name;
    private int iconId;
    private List<Integer> itemIds = emptyList();
    private List<Integer> recipeIds = emptyList();
    private List<Integer> recipeModifierIds = emptyList();
    private List<Integer> machineIds = emptyList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Integer> itemIds) {
        this.itemIds = itemIds;
    }

    public List<Integer> getRecipeIds() {
        return recipeIds;
    }

    public void setRecipeIds(List<Integer> recipeIds) {
        this.recipeIds = recipeIds;
    }

    public List<Integer> getRecipeModifierIds() {
        return recipeModifierIds;
    }

    public void setRecipeModifierIds(List<Integer> recipeModifierIds) {
        this.recipeModifierIds = recipeModifierIds;
    }

    public List<Integer> getMachineIds() {
        return machineIds;
    }

    public void setMachineIds(List<Integer> machineIds) {
        this.machineIds = machineIds;
    }

}
