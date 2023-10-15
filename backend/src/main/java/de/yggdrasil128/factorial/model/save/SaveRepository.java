package de.yggdrasil128.factorial.model.save;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SaveRepository extends CrudRepository<Save, Integer> {

    Optional<Save> findByFactoriesId(int id);

}
