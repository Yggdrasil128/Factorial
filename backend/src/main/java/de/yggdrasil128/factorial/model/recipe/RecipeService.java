package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
public class RecipeService extends ModelService<Recipe, RecipeRepository> {

    private final IconService icons;
    private final ResourceService resources;

    public RecipeService(RecipeRepository repository, IconService icons, ResourceService resources) {
        super(repository);
        this.icons = icons;
        this.resources = resources;
    }

    public Recipe create(GameVersion gameVersion, RecipeInput input) {
        Icon icon = OptionalInputField.ofId(input.getIconId(), icons::get).get();
        List<Resource> inputs = OptionalInputField.of(input.getInput()).map(resources::get).get();
        List<Resource> outputs = OptionalInputField.of(input.getOutput()).map(resources::get).get();
        List<String> category = OptionalInputField.of(input.getCategory()).get();
        return repository.save(new Recipe(gameVersion, input.getName(), icon, inputs, outputs, input.getDuration(),
                emptyList(), emptyList(), category));
    }

    public Recipe update(int id, RecipeInput input) {
        Recipe recipe = get(id);
        OptionalInputField.of(input.getName()).apply(recipe::setName);
        OptionalInputField.ofId(input.getIconId(), icons::get).apply(recipe::setIcon);
        OptionalInputField.of(input.getInput()).map(resources::get).apply(recipe::setInput);
        OptionalInputField.of(input.getOutput()).map(resources::get).apply(recipe::setOutput);
        if (null != input.getDuration()) {
            recipe.setDuration(input.getDuration());
        }
        OptionalInputField.of(input.getCategory()).apply(recipe::setCategory);
        return repository.save(recipe);
    }

}
