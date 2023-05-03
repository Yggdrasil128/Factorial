package de.yggdrasil128.factorial;

import de.yggdrasil128.factorial.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FooService {
    private ItemRepository itemRepository;

    @Autowired
    public FooService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public long getItemCount() {
        return itemRepository.count();
    }
}
