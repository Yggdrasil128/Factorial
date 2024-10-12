package de.yggdrasil128.factorial.model.gameversion;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Collections.emptyList;

@Service
public class GameVersionService extends ModelService<GameVersion, GameVersionRepository> {

    private final IconService icons;

    public GameVersionService(GameVersionRepository repository, IconService icons) {
        super(repository);
        this.icons = icons;
    }

    public GameVersion create(GameVersion gameVersion) {
        return repository.save(gameVersion);
    }

    public GameVersion create(GameVersionInput input) {
        Icon icon = OptionalInputField.ofId(input.getIconId(), icons::get).get();
        return repository.save(new GameVersion(input.getName(), icon, emptyList(), emptyList(), emptyList(),
                emptyList(), emptyList()));
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

    public GameVersion update(int id, GameVersionInput input) {
        GameVersion gameVersion = get(id);
        OptionalInputField.of(input.getName()).apply(gameVersion::setName);
        OptionalInputField.ofId(input.getIconId(), icons::get).apply(gameVersion::setIcon);
        return repository.save(gameVersion);
    }

}
