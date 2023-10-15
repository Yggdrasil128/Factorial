package de.yggdrasil128.factorial.model.item;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ItemRepository extends CrudRepository<Item, Integer> {

    Optional<Item> findByGameVersionIdAndName(int gameVersionId, String name);

}
