package de.yggdrasil128.factorial.model.resource.local;

import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import de.yggdrasil128.factorial.model.item.ItemService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toMap;

@Service
public class LocalResourceService extends ParentedModelService<LocalResource, LocalResourceStandalone, Factory, LocalResourceRepository> {

    private final ApplicationEventPublisher events;
    private final FactoryRepository factoryRepository;
    private final ItemService itemService;
    private final Map<Integer, ResourceContributions> cache = new HashMap<>();

    public LocalResourceService(LocalResourceRepository repository, ApplicationEventPublisher events,
                           FactoryRepository factoryRepository, ItemService itemService) {
        super(repository);
        this.events = events;
        this.factoryRepository = factoryRepository;
        this.itemService = itemService;
    }

    @Override
    protected int getEntityId(LocalResource resource) {
        return resource.getId();
    }

    @Override
    protected int getStandaloneId(LocalResourceStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Factory getParentEntity(int parentId) {
        return factoryRepository.findById(parentId).orElseThrow(ModelService::reportNotFound);
    }

    @Override
    protected LocalResource prepareCreate(Factory factory, LocalResourceStandalone standalone) {
        LocalResource resource = new LocalResource(factory, standalone);
        OptionalInputField.ofId(standalone.itemId(), itemService::get).apply(resource::setItem);
        inferOrdinal(factory, resource);
        return resource;
    }

    @Override
    protected void handleBulkCreate(Factory factory, Iterable<LocalResource> resources) {
        for (LocalResource resource : resources) {
            factory.getResources().add(resource);
            events.publishEvent(new LocalResourceUpdatedEvent(resource, false));
        }
        factoryRepository.save(factory);
    }

    public ResourceContributions spawn(int factoryId, int itemId) {
        Factory factory = factoryRepository.findById(factoryId).orElseThrow(ModelService::reportNotFound);
        LocalResource resource = new LocalResource();
        resource.setFactory(factory);
        resource.setItem(itemService.get(itemId));
        inferOrdinal(factory, resource);
        resource = repository.save(resource);
        factory.getResources().add(resource);
        /*
         * We are called exclusively from ProductionLineService#spawn(), which will then take care of further
         * propagating the consequences of a resource being created. Therefore, we do not save the factory here nor do
         * we publish an event.
         */
        return computeContributions(resource);
    }

    private static void inferOrdinal(Factory factory, LocalResource resource) {
        if (0 >= resource.getOrdinal()) {
            resource.setOrdinal(factory.getResources().stream().mapToInt(LocalResource::getOrdinal).max().orElse(0) + 1);
        }
    }

    public ResourceContributions computeContributions(LocalResource resource) {
        return cache.computeIfAbsent(resource.getId(), key -> new ResourceContributions(resource));
    }

    @Override
    protected void prepareUpdate(LocalResource resource, LocalResourceStandalone standalone) {
        if (null != standalone.itemId() && (int) standalone.itemId() != resource.getItem().getId()) {
            throw report(HttpStatus.NOT_IMPLEMENTED, "cannot change item of a resource");
        }
        resource.applyBasics(standalone);
    }

    @Override
    protected void handleUpdate(LocalResource resource) {
        events.publishEvent(new LocalResourceUpdatedEvent(resource, false));
    }

    public void updateContributions(ResourceContributions contributions) {
        events.publishEvent(new LocalResourceContributionsChangedEvent(get(contributions.getResourceId()), contributions));
    }

    @Override
    protected Factory findParentEntity(int id) {
        Factory factory = factoryRepository.findByResourcesId(id);
        if (null == factory) {
            throw report(HttpStatus.CONFLICT, "resource does not belong to a factory");
        }
        factory.getResources().remove(get(id));
        return factory;
    }

    public void destroy(Factory factory, int id) {
        factory.getResources().remove(get(id));
        repository.deleteById(id);
        handleDelete(factory, id);
    }

    @Override
    protected void handleDelete(Factory factory, int id) {
        cache.remove(id);
        events.publishEvent(new LocalResourceRemovedEvent(factory.getSave().getId(), factory.getId(), id));
    }

    @Transactional
    public void reorder(int factoryId, List<EntityPosition> input, CompletableFuture<Void> result) {
        Factory factory = factoryRepository.findById(factoryId).orElseThrow(ModelService::reportNotFound);
        Map<Integer, Integer> order = input.stream().collect(toMap(EntityPosition::id, EntityPosition::ordinal));
        AsyncHelper.complete(result);
        Collection<LocalResource> resources = new ArrayList<>();
        for (LocalResource resource : factory.getResources()) {
            Integer ordinal = order.get(resource.getId());
            if (null != ordinal) {
                resource.setOrdinal(ordinal.intValue());
                resources.add(resource);
                repository.save(resource);
            }
        }
        events.publishEvent(new LocalResourcesReorderedEvent(factory.getSave().getId(), resources));
    }

    @EventListener
    public void on(LocalResourceUpdatedEvent event) {
        ResourceContributions contributions = cache.get(event.getResource().getId());
        if (null != contributions) {
            contributions.update(event.getResource());
        }
    }

}
