package de.yggdrasil128.factorial.repository;

import de.yggdrasil128.factorial.model.Recipe;
import org.springframework.data.repository.CrudRepository;

public interface RecipeRepository extends CrudRepository<Recipe, Integer> {
}
