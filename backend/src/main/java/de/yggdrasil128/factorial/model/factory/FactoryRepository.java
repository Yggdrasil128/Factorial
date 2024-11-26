package de.yggdrasil128.factorial.model.factory;

import org.springframework.data.repository.CrudRepository;

public interface FactoryRepository extends CrudRepository<Factory, Integer> {

    boolean existsBySaveIdAndName(int saveId, String name);

    Factory findByProductionStepsId(int id);

    Factory findByResourcesId(int id);

}
