package de.yggdrasil128.factorial.model.recipe;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface RecipeRepository extends CrudRepository<Recipe, Integer> {

    Optional<Recipe> findByGameVersionIdAndName(int gameVersionId, String name);

}
