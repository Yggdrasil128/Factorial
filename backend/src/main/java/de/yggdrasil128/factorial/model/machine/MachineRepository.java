package de.yggdrasil128.factorial.model.machine;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MachineRepository extends CrudRepository<Machine, Integer> {

    Optional<Machine> findByGameVersionIdAndName(int gameVersionId, String name);

}
