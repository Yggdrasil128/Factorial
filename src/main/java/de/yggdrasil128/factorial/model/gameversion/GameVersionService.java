package de.yggdrasil128.factorial.model.gameversion;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Service;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemMigration;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.machine.MachineMigration;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeMigration;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierMigration;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;

@Service
public class GameVersionService extends ModelService<GameVersion, GameVersionRepository> {

    private final IconService icons;
    private final ItemService items;
    private final RecipeService recipies;
    private final RecipeModifierService recipeModifiers;
    private final MachineService machines;

    public GameVersionService(GameVersionRepository repository, IconService icons, ItemService items,
        RecipeService recipies, RecipeModifierService recipeModifiers, MachineService machines) {
        super(repository);
        this.icons = icons;
        this.items = items;
        this.recipies = recipies;
        this.recipeModifiers = recipeModifiers;
        this.machines = machines;
    }

    public GameVersion get(Game game, String name) {
        return repository.findByGameIdAndName(game.getId(), name).orElseThrow(ModelService::reportNotFound);
    }

    public GameVersion create(Game game, GameVersionStandalone input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        return repository
            .save(new GameVersion(game, input.getName(), icon, emptyList(), emptyList(), emptyList(), emptyList()));
    }

    public GameVersion doImport(Game game, String name, GameVersionMigration input) {
        GameVersion gameVersion = new GameVersion(game, name, null, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        for (Map.Entry<String, ItemMigration> entry : input.getItems().entrySet()) {
            gameVersion.getItems().add(items.doImport(gameVersion, entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<String, RecipeMigration> entry : input.getRecipies().entrySet()) {
            gameVersion.getRecipies().add(recipies.doImport(gameVersion, entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<String, RecipeModifierMigration> entry : input.getRecipeModifiers().entrySet()) {
            gameVersion.getRecipeModifiers().add(recipeModifiers.doImport(gameVersion, entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<String, MachineMigration> entry : input.getMachines().entrySet()) {
            gameVersion.getMachines().add(machines.doImport(gameVersion, entry.getKey(), entry.getValue()));
        }
        return gameVersion;
    }

    public Item addItem(int gameVersionId, ItemStandalone input) {
        GameVersion gameVersion = get(gameVersionId);
        Item item = items.create(gameVersion, input);
        gameVersion.getItems().add(item);
        repository.save(gameVersion);
        return item;
    }

    public Recipe addRecipe(int gameVersionId, RecipeStandalone input) {
        GameVersion gameVersion = get(gameVersionId);
        Recipe recipe = recipies.create(gameVersion, input);
        gameVersion.getRecipies().add(recipe);
        repository.save(gameVersion);
        return recipe;
    }

    public RecipeModifier addRecipeModifier(int gameVersionId, RecipeModifierStandalone input) {
        GameVersion gameVersion = get(gameVersionId);
        RecipeModifier recipeModifier = recipeModifiers.create(gameVersion, input);
        gameVersion.getRecipeModifiers().add(recipeModifier);
        repository.save(gameVersion);
        return recipeModifier;
    }

    public Machine addMachine(int gameVersionId, MachineStandalone input) {
        GameVersion gameVersion = get(gameVersionId);
        Machine machine = machines.create(gameVersion, input);
        gameVersion.getMachines().add(machine);
        repository.save(gameVersion);
        return machine;
    }

}
