package de.yggdrasil128.factorial.repository;

import de.yggdrasil128.factorial.model.Machine;
import org.springframework.data.repository.CrudRepository;

public interface MachineRepository extends CrudRepository<Machine, Integer> {
}
