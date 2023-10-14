package de.yggdrasil128.factorial.model.icon;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IconRepository extends CrudRepository<Icon, Integer> {

    Optional<Icon> findByGameVersionIdAndName(int gameVersionId, String name);

}
