package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import de.yggdrasil128.factorial.model.item.Item;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class ResourceService extends ModelService<Resource, ResourceRepository> {

    private final ApplicationEventPublisher events;
    private final FactoryRepository factories;
    private final Map<Integer, ResourceContributions> cache = new HashMap<>();

    public ResourceService(ResourceRepository repository, ApplicationEventPublisher events,
                           FactoryRepository factories) {
        super(repository);
        this.events = events;
        this.factories = factories;
    }

    @Override
    public Resource create(Resource entity) {
        if (0 >= entity.getOrdinal()) {
            entity.setOrdinal(
                    entity.getFactory().getResources().stream().mapToInt(Resource::getOrdinal).max().orElse(0) + 1);
        }
        return super.create(entity);
    }

    public ResourceContributions spawn(Factory factory, Item item) {
        Resource resource = new Resource();
        resource.setFactory(factory);
        resource.setItem(item);
        resource = create(resource);
        ResourceContributions contributions = new ResourceContributions(resource);
        cache.put(resource.getId(), contributions);
        return contributions;
    }

    public void reorder(Factory factory, List<ReorderInputEntry> input) {
        Map<Integer, Integer> order = input.stream()
                .collect(toMap(ReorderInputEntry::getId, ReorderInputEntry::getOrdinal));
        for (Resource resource : factory.getResources()) {
            Integer ordinal = order.get(resource.getId());
            if (null != ordinal) {
                resource.setOrdinal(ordinal.intValue());
                repository.save(resource);
            }
        }
    }

    @Override
    public void delete(int id) {
        Factory factory = factories.findByResourcesId(id);
        super.delete(id);
        cache.remove(id);
        events.publishEvent(new ResourceRemovedEvent(factory.getSave().getId(), factory.getId(), id));
    }

}
