package de.yggdrasil128.factorial.model.save;

import org.springframework.data.repository.CrudRepository;

public interface SaveRepository extends CrudRepository<Save, Integer> {

    Save findByFactoriesId(int id);

}
