package de.yggdrasil128.factorial.model.item;

import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Integer> {

    boolean existsByGameIdAndName(int gameId, String name);

}
