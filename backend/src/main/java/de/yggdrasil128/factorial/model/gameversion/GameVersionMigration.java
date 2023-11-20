package de.yggdrasil128.factorial.model.gameversion;

import de.yggdrasil128.factorial.model.icon.IconMigration;
import de.yggdrasil128.factorial.model.item.ItemMigration;
import de.yggdrasil128.factorial.model.machine.MachineMigration;
import de.yggdrasil128.factorial.model.recipe.RecipeMigration;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierMigration;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class GameVersionMigration {

    private String game;
    private String version;
    private String iconName;
    private Map<String, IconMigration> icons = emptyMap();
    private Map<String, ItemMigration> items = emptyMap();
    private Map<String, RecipeMigration> recipes = emptyMap();
    private Map<String, RecipeModifierMigration> recipeModifiers = emptyMap();
    private Map<String, MachineMigration> machines = emptyMap();

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public Map<String, IconMigration> getIcons() {
        return icons;
    }

    public void setIcons(Map<String, IconMigration> icons) {
        this.icons = icons;
    }

    public Map<String, ItemMigration> getItems() {
        return items;
    }

    public void setItems(Map<String, ItemMigration> items) {
        this.items = items;
    }

    public Map<String, RecipeMigration> getRecipes() {
        return recipes;
    }

    public void setRecipes(Map<String, RecipeMigration> recipes) {
        this.recipes = recipes;
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
