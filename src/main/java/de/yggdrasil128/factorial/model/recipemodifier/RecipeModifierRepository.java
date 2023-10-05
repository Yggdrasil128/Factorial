package de.yggdrasil128.factorial.model.recipemodifier;

import org.springframework.data.repository.CrudRepository;

public interface RecipeModifierRepository extends CrudRepository<RecipeModifier, Integer> {

    public Iterable<RecipeModifier> getAllByGameVersionIdAndNameIn(int gameVersionId, Iterable<String> names);

}
