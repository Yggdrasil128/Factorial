package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.model.item.ItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    private final ItemService items;

    public ResourceService(ItemService items) {
        this.items = items;
    }

    public Resource get(ResourceInput resource) {
        return new Resource(items.get(resource.getItemId()), resource.getQuantity());
    }

    public List<Resource> get(List<ResourceInput> resources) {
        return resources.stream().map(this::get).toList();
    }

}
