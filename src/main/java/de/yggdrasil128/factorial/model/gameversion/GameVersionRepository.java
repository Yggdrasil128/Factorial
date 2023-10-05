package de.yggdrasil128.factorial.model.gameversion;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameVersionRepository extends CrudRepository<GameVersion, Integer> {

    Optional<GameVersion> findByGameIdAndName(int gameId, String name);

}
