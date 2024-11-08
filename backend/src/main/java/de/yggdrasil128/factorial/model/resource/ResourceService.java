package de.yggdrasil128.factorial.model.resource;

import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.EntityPosition;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import de.yggdrasil128.factorial.model.item.ItemService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toMap;

@Service
public class ResourceService extends ModelService<Resource, ResourceRepository> {

    private final ApplicationEventPublisher events;
    private final FactoryRepository factoryRepository;
    private final ItemService itemService;
    private final Map<Integer, ResourceContributions> cache = new HashMap<>();

    public ResourceService(ResourceRepository repository, ApplicationEventPublisher events,
                           FactoryRepository factoryRepository, ItemService itemService) {
        super(repository);
        this.events = events;
        this.factoryRepository = factoryRepository;
        this.itemService = itemService;
    }

    public void create(int factoryId, ResourceStandalone standalone) {
        Factory factory = factoryRepository.findById(factoryId).orElseThrow(ModelService::reportNotFound);
        Resource resource = new Resource(factory, standalone);
        applyRelations(resource, standalone);
        inferOrdinal(factory, resource);
        resource = create(resource);
        factory.getResources().add(resource);
        factoryRepository.save(factory);
        events.publishEvent(new ResourceUpdatedEvent(resource, false));
    }

    private void applyRelations(Resource resource, ResourceStandalone standalone) {
        OptionalInputField.ofId(standalone.itemId(), itemService::get).apply(resource::setItem);
    }

    public ResourceContributions spawn(int factoryId, int itemId) {
        Factory factory = factoryRepository.findById(factoryId).orElseThrow(ModelService::reportNotFound);
        Resource resource = new Resource();
        resource.setFactory(factory);
        resource.setItem(itemService.get(itemId));
        inferOrdinal(factory, resource);
        resource = create(resource);
        factory.getResources().add(resource);
        /*
         * We are called exclusively from ProductionLineService#spawn(), which will then take care of further
         * propagating the consequences of a resource being created. Therefore, we do not save the factory here nor do
         * we publish an event.
         */
        return computeContributions(resource);
    }

    private static void inferOrdinal(Factory factory, Resource resource) {
        if (0 >= resource.getOrdinal()) {
            resource.setOrdinal(factory.getResources().stream().mapToInt(Resource::getOrdinal).max().orElse(0) + 1);
        }
    }

    public ResourceContributions computeContributions(Resource resource) {
        return cache.computeIfAbsent(resource.getId(), key -> new ResourceContributions(resource));
    }

    public void reorder(int factoryId, List<EntityPosition> input) {
        Factory factory = factoryRepository.findById(factoryId).orElseThrow(ModelService::reportNotFound);
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

    public void update(int id, ResourceStandalone standalone) {
        Resource resource = get(id);
        resource.applyBasics(standalone);
        if (null != standalone.itemId() && (int) standalone.itemId() != resource.getItem().getId()) {
            throw report(HttpStatus.NOT_IMPLEMENTED, "cannot change item of a resource");
        }
        resource = update(resource);
        events.publishEvent(new ResourceUpdatedEvent(resource, false));
    }

    @Override
    public void delete(int id) {
        Factory factory = factoryRepository.findByResourcesId(id);
        if (null == factory) {
            throw report(HttpStatus.CONFLICT, "resource does not belong to a factory");
        }
        super.delete(id);
        cache.remove(id);
        events.publishEvent(new ResourceRemovedEvent(factory.getSave().getId(), factory.getId(), id));
    }

    @EventListener
    public void on(ResourceUpdatedEvent event) {
        ResourceContributions contributions = cache.get(event.getResource().getId());
        if (null != contributions) {
            contributions.update(event.getResource());
        }
    }

}
