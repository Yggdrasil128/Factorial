package de.yggdrasil128.factorial.model.icon;

import org.springframework.data.repository.CrudRepository;

public interface IconRepository extends CrudRepository<Icon, Integer> {

    boolean existsByGameIdAndName(int gameId, String name);

}
