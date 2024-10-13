package de.yggdrasil128.factorial.model.gameversion;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameVersionService extends ModelService<GameVersion, GameVersionRepository> {

    public GameVersionService(GameVersionRepository repository) {
        super(repository);
    }

    public Optional<GameVersion> get(String name) {
        return repository.findByName(name);
    }

    public void addAttachedIcon(GameVersion gameVersion, Icon icon) {
        gameVersion.getIcons().add(icon);
        repository.save(gameVersion);
    }

    public void addAttachedItem(GameVersion gameVersion, Item item) {
        gameVersion.getItems().add(item);
        repository.save(gameVersion);
    }

    public void addAttachedRecipe(GameVersion gameVersion, Recipe recipe) {
        gameVersion.getRecipes().add(recipe);
    }

    public void addAttachedRecipeModifier(GameVersion gameVersion, RecipeModifier recipeModifier) {
        gameVersion.getRecipeModifiers().add(recipeModifier);
        repository.save(gameVersion);
    }

    public void addAttachedMachine(GameVersion gameVersion, Machine machine) {
        gameVersion.getMachines().add(machine);
        repository.save(gameVersion);
    }

}
