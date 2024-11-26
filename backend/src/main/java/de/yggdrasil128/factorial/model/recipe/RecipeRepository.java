package de.yggdrasil128.factorial.model.recipe;

import org.springframework.data.repository.CrudRepository;

public interface RecipeRepository extends CrudRepository<Recipe, Integer> {

    boolean existsByGameIdAndName(int gameId, String name);

}
