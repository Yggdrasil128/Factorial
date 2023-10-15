package de.yggdrasil128.factorial.model.gameversion;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class GameVersionService extends ModelService<GameVersion, GameVersionRepository> {

    private final IconService icons;

    public GameVersionService(GameVersionRepository repository, IconService icons) {
        super(repository);
        this.icons = icons;
    }

    public GameVersion create(Game game, GameVersionInput input) {
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        return repository.save(new GameVersion(game, input.getName(), icon, emptyList(), emptyList(), emptyList(),
                emptyList(), emptyList()));
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

    public GameVersion update(int id, GameVersionInput input) {
        GameVersion gameVersion = get(id);
        if (null != input.getName()) {
            gameVersion.setName(input.getName());
        }
        if (0 != input.getIconId()) {
            gameVersion.setIcon(icons.get(input.getIconId()));
        }
        return repository.save(gameVersion);
    }

}
