package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.ModelService;
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
        Icon icon = 0 == input.getIconId() ? null : icons.get(input.getIconId());
        List<Resource> inputs = null == input.getInput() ? emptyList() : resources.get(input.getInput());
        List<Resource> outputs = null == input.getOutput() ? emptyList() : resources.get(input.getOutput());
        List<String> category = null == input.getCategory() ? emptyList() : input.getCategory();
        return repository.save(new Recipe(gameVersion, input.getName(), icon, inputs, outputs, input.getDuration(),
                emptyList(), emptyList(), category));
    }

    public Recipe update(int id, RecipeInput input) {
        Recipe recipe = get(id);
        if (null != input.getName()) {
            recipe.setName(input.getName());
        }
        if (0 != input.getIconId()) {
            recipe.setIcon(icons.get(input.getIconId()));
        }
        if (null != input.getInput()) {
            recipe.setInput(resources.get(input.getInput()));
        }
        if (null != input.getOutput()) {
            recipe.setOutput(resources.get(input.getOutput()));
        }
        if (null != input.getDuration()) {
            recipe.setDuration(input.getDuration());
        }
        if (null != input.getCategory()) {
            recipe.setCategory(input.getCategory());
        }
        return repository.save(recipe);
    }

}
