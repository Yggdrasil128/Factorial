package de.yggdrasil128.factorial.model.machine;

import org.springframework.data.repository.CrudRepository;

public interface MachineRepository extends CrudRepository<Machine, Integer> {

    boolean existsByGameIdAndName(int gameId, String name);

}
