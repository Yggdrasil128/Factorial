package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.ModelService;
import org.springframework.stereotype.Service;

@Service
public class RecipeModifierService extends ModelService<RecipeModifier, RecipeModifierRepository> {

    public RecipeModifierService(RecipeModifierRepository repository) {
        super(repository);
    }

}
