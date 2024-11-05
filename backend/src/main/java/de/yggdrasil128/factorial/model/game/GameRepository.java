package de.yggdrasil128.factorial.model.game;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Integer> {

    Optional<Game> findByName(String name);

    Game findByIconsId(int id);

    Game findByItemsId(int id);

    Game findByRecipesId(int id);

    Game findByRecipeModifiersId(int id);

    Game findByMachinesId(int id);

}
