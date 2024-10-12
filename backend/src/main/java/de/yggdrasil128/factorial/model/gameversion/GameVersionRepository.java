package de.yggdrasil128.factorial.model.gameversion;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameVersionRepository extends CrudRepository<GameVersion, Integer> {

    Optional<GameVersion> findByName(String name);

}
