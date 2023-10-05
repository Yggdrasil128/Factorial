package de.yggdrasil128.factorial.model.game;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, Integer> {

    public Optional<Game> findByName(String name);

}
