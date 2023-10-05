package de.yggdrasil128.factorial.model.machine;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface MachineRepository extends CrudRepository<Machine, Integer> {

    Optional<Machine> findByGameVersionIdAndName(int gameVersionId, String name);

}
