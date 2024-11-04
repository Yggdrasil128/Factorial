package de.yggdrasil128.factorial.model.game;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Integer> {

    Optional<Game> findByName(String name);

}
