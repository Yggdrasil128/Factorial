package de.yggdrasil128.factorial.repository;

import de.yggdrasil128.factorial.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Integer> {
}
