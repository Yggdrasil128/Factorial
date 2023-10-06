package de.yggdrasil128.factorial.model.recipe;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe, Integer> {

    Optional<Recipe> findByGameVersionIdAndName(int gameVersionId, String name);

}
