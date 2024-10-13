package de.yggdrasil128.factorial.model.item;

import de.yggdrasil128.factorial.model.ModelService;
import org.springframework.stereotype.Service;

@Service
public class ItemService extends ModelService<Item, ItemRepository> {

    public ItemService(ItemRepository repository) {
        super(repository);
    }

    public Item create(Item item) {
        return repository.save(item);
    }

    public Item update(Item item) {
        return repository.save(item);
    }

}
