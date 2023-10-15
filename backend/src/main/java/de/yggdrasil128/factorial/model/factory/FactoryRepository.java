package de.yggdrasil128.factorial.model.factory;

import org.springframework.data.repository.CrudRepository;

public interface FactoryRepository extends CrudRepository<Factory, Integer> {

    int countBySaveId(int saveId);

}
