package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.ModelService;
import org.springframework.stereotype.Service;

@Service
public class RecipeService extends ModelService<Recipe, RecipeRepository> {

    public RecipeService(RecipeRepository repository) {
        super(repository);
    }

}
