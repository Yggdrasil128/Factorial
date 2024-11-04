package de.yggdrasil128.factorial.model.game;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService extends ModelService<Game, GameRepository> {

    public GameService(GameRepository repository) {
        super(repository);
    }

    public Optional<Game> get(String name) {
        return repository.findByName(name);
    }

    public void addAttachedIcon(Game game, Icon icon) {
        game.getIcons().add(icon);
        repository.save(game);
    }

    public void addAttachedItem(Game game, Item item) {
        game.getItems().add(item);
        repository.save(game);
    }

    public void addAttachedRecipe(Game game, Recipe recipe) {
        game.getRecipes().add(recipe);
    }

    public void addAttachedRecipeModifier(Game game, RecipeModifier recipeModifier) {
        game.getRecipeModifiers().add(recipeModifier);
        repository.save(game);
    }

    public void addAttachedMachine(Game game, Machine machine) {
        game.getMachines().add(machine);
        repository.save(game);
    }

}
