package de.yggdrasil128.factorial.model.resource.global;

import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toMap;

@Service
public class GlobalResourceService
        extends ParentedModelService<GlobalResource, GlobalResourceStandalone, Save, GlobalResourceRepository> {

    private final ApplicationEventPublisher events;
    private final SaveRepository saveRepository;
    private final ItemService itemService;
    private final Map<Integer, ResourceContributions> cache = new HashMap<>();

    public GlobalResourceService(GlobalResourceRepository repository, ApplicationEventPublisher events,
                               SaveRepository saveRepository, ItemService itemService) {
        super(repository);
        this.events = events;
        this.saveRepository = saveRepository;
        this.itemService = itemService;
    }

    @Override
    protected int getEntityId(GlobalResource resource) {
        return resource.getId();
    }

    @Override
    protected int getStandaloneId(GlobalResourceStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Save getParentEntity(int parentId) {
        return saveRepository.findById(parentId).orElseThrow(ModelService::reportNotFound);
    }

    @Override
    protected GlobalResource prepareCreate(Save save, GlobalResourceStandalone standalone) {
        GlobalResource resource = new GlobalResource(save, standalone);
        OptionalInputField.ofId(standalone.itemId(), itemService::get).apply(resource::setItem);
        inferOrdinal(save, resource);
        return resource;
    }

    @Override
    protected void handleBulkCreate(Save save, Iterable<GlobalResource> resources) {
        for (GlobalResource resource : resources) {
            save.getResources().add(resource);
            events.publishEvent(new GlobalResourceUpdatedEvent(resource, false));
        }
        saveRepository.save(save);
    }

    public ResourceContributions spawn(int saveId, int itemId) {
        Save save = saveRepository.findById(saveId).orElseThrow(ModelService::reportNotFound);
        GlobalResource resource = new GlobalResource();
        resource.setSave(save);
        resource.setItem(itemService.get(itemId));
        inferOrdinal(save, resource);
        resource = repository.save(resource);
        save.getResources().add(resource);
        /*
         * We are called exclusively from ProductionLineService#spawn(), which will then take care of further
         * propagating the consequences of a resource being created. Therefore, we do not save the factory here nor do
         * we publish an event.
         */
        return computeContributions(resource);
    }

    private static void inferOrdinal(Save save, GlobalResource resource) {
        if (0 >= resource.getOrdinal()) {
            resource.setOrdinal(save.getResources().stream().mapToInt(GlobalResource::getOrdinal).max().orElse(0) + 1);
        }
    }

    public ResourceContributions computeContributions(GlobalResource resource) {
        return cache.computeIfAbsent(resource.getId(), key -> new ResourceContributions(resource));
    }

    @Override
    protected void prepareUpdate(GlobalResource resource, GlobalResourceStandalone standalone) {
        if (null != standalone.itemId() && (int) standalone.itemId() != resource.getItem().getId()) {
            throw report(HttpStatus.NOT_IMPLEMENTED, "cannot change item of a resource");
        }
        resource.applyBasics(standalone);
    }

    @Override
    protected void handleUpdate(GlobalResource resource) {
        events.publishEvent(new GlobalResourceUpdatedEvent(resource, false));
    }

    public void updateContributions(ResourceContributions contributions) {
        events.publishEvent(
                new GlobalResourceContributionsChangedEvent(get(contributions.getResourceId()), contributions));
    }

    @Override
    protected Save findParentEntity(int id) {
        Save save = saveRepository.findByResourcesId(id);
        if (null == save) {
            throw report(HttpStatus.CONFLICT, "resource does not belong to a factory");
        }
        save.getResources().remove(get(id));
        return save;
    }

    public void destroy(Save save, int id) {
        save.getResources().remove(get(id));
        repository.deleteById(id);
        handleDelete(save, id);
    }

    @Override
    protected void handleDelete(Save save, int id) {
        cache.remove(id);
        events.publishEvent(new GlobalResourceRemovedEvent(save.getId(), id));
    }

    @Transactional
    public void reorder(int factoryId, List<EntityPosition> input, CompletableFuture<Void> result) {
        Save save = saveRepository.findById(factoryId).orElseThrow(ModelService::reportNotFound);
        Map<Integer, Integer> order = input.stream().collect(toMap(EntityPosition::id, EntityPosition::ordinal));
        AsyncHelper.complete(result);
        Collection<GlobalResource> resources = new ArrayList<>();
        for (GlobalResource resource : save.getResources()) {
            Integer ordinal = order.get(resource.getId());
            if (null != ordinal) {
                resource.setOrdinal(ordinal.intValue());
                resources.add(resource);
                repository.save(resource);
            }
        }
        events.publishEvent(new GlobalResourcesReorderedEvent(save.getId(), resources));
    }

}
