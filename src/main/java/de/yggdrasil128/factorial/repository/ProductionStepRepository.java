package de.yggdrasil128.factorial.repository;

import de.yggdrasil128.factorial.model.ProductionStep;
import org.springframework.data.repository.CrudRepository;

public interface ProductionStepRepository extends CrudRepository<ProductionStep, Integer> {
}
