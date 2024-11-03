package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;

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
        /*
         * We are called exclusively from ProductionLineService#spawn(), which will then take care of further
         * propagating the consequences of a resource being created. Therefore, we do not publish an event here.
         */
    }

    public ResourceContributions computeContributions(Resource resource) {
        return cache.computeIfAbsent(resource.getId(), key -> new ResourceContributions(resource));
    }

    public void reorder(Factory factory, List<EntityPosition> input) {
        Map<Integer, Integer> order = input.stream().collect(toMap(EntityPosition::id, EntityPosition::ordinal));
        Collection<Resource> resources = new ArrayList<>();
        for (Resource resource : factory.getResources()) {
            Integer ordinal = order.get(resource.getId());
            if (null != ordinal) {
                resource.setOrdinal(ordinal.intValue());
                resources.add(resource);
                repository.save(resource);
            }
        }
        events.publishEvent(new ResourcesReorderedEvent(factory.getSave().getId(), resources));
    }

    public void updateContributions(ResourceContributions contributions) {
        events.publishEvent(new ResourceContributionsChangedEvent(get(contributions.getResourceId()), contributions));
    }

    @Override
    public Resource update(Resource entity) {
        Resource resource = super.update(entity);
        events.publishEvent(new ResourceUpdatedEvent(resource));
        return resource;
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
