package de.yggdrasil128.factorial.model.save;

import org.springframework.data.repository.CrudRepository;

public interface SaveRepository extends CrudRepository<Save, Integer> {

    Save findByFactoriesId(int id);

    Save findByChangelistsId(int id);

    Save findByResourcesId(int id);

}
