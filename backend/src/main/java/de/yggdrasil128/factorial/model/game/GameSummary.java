package de.yggdrasil128.factorial.model.game;

import de.yggdrasil128.factorial.model.icon.IconStandalone;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;

import java.util.List;

public class GameSummary {

    private GameStandalone game;
    private List<IconStandalone> icons;
    private List<ItemStandalone> items;
    private List<RecipeStandalone> recipes;
    private List<RecipeModifierStandalone> recipeModifiers;
    private List<MachineStandalone> machines;

    public GameStandalone getGame() {
        return game;
    }

    public void setGame(GameStandalone game) {
        this.game = game;
    }

    public List<IconStandalone> getIcons() {
        return icons;
    }

    public void setIcons(List<IconStandalone> icons) {
        this.icons = icons;
    }

    public List<ItemStandalone> getItems() {
        return items;
    }

    public void setItems(List<ItemStandalone> items) {
        this.items = items;
    }

    public List<RecipeStandalone> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipeStandalone> recipes) {
        this.recipes = recipes;
    }

    public List<RecipeModifierStandalone> getRecipeModifiers() {
        return recipeModifiers;
    }

    public void setRecipeModifiers(List<RecipeModifierStandalone> recipeModifiers) {
        this.recipeModifiers = recipeModifiers;
    }

    public List<MachineStandalone> getMachines() {
        return machines;
    }

    public void setMachines(List<MachineStandalone> machines) {
        this.machines = machines;
    }

}
