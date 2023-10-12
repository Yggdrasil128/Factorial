package de.yggdrasil128.factorial.model.changelist;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangelistRepository extends CrudRepository<Changelist, Integer> {
}
