package de.yggdrasil128.factorial.model.gameversion;

import de.yggdrasil128.factorial.model.item.ItemMigration;
import de.yggdrasil128.factorial.model.machine.MachineMigration;
import de.yggdrasil128.factorial.model.recipe.RecipeMigration;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierMigration;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class GameVersionMigration {

    private Map<String, ItemMigration> items = emptyMap();
    private Map<String, RecipeMigration> recipies = emptyMap();
    private Map<String, RecipeModifierMigration> recipeModifiers = emptyMap();
    private Map<String, MachineMigration> machines = emptyMap();

    public Map<String, ItemMigration> getItems() {
        return items;
    }

    public void setItems(Map<String, ItemMigration> items) {
        this.items = items;
    }

    public Map<String, RecipeMigration> getRecipies() {
        return recipies;
    }

    public void setRecipies(Map<String, RecipeMigration> recipies) {
        this.recipies = recipies;
    }

    public Map<String, RecipeModifierMigration> getRecipeModifiers() {
        return recipeModifiers;
    }

    public void setRecipeModifiers(Map<String, RecipeModifierMigration> recipeModifiers) {
        this.recipeModifiers = recipeModifiers;
    }

    public Map<String, MachineMigration> getMachines() {
        return machines;
    }

    public void setMachines(Map<String, MachineMigration> machines) {
        this.machines = machines;
    }

}
