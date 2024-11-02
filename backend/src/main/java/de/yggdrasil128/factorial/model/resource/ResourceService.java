package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
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

    public ResourceContributions computeContributions(Resource resource) {
        return cache.computeIfAbsent(resource.getId(), key -> new ResourceContributions(resource));
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

    public void updateContributions(int id, ResourceContributions contributions) {
        events.publishEvent(new ResourceContributionsChangedEvent(get(id), contributions));
    }

    @Override
    public void delete(int id) {
        Factory factory = factories.findByResourcesId(id);
        super.delete(id);
        cache.remove(id);
        if (null != factory) {
            events.publishEvent(new ResourceRemovedEvent(factory.getSave().getId(), factory.getId(), id));
        }
    }

    @EventListener
    public void on(ResourceUpdatedEvent event) {
        ResourceContributions contributions = cache.get(event.getResource().getId());
        if (null != contributions) {
            contributions.update(event.getResource());
        }
    }

}
