package de.yggdrasil128.factorial.model.recipemodifier;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecipeModifierRepository extends CrudRepository<RecipeModifier, Integer> {

    List<RecipeModifier> findAllByGameVersionIdAndNameIn(int gameVersionId, List<String> names);

}
