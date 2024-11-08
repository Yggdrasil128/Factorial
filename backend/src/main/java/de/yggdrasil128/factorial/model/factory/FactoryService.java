package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.productionstep.*;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceService;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;
import de.yggdrasil128.factorial.model.resource.ResourceUpdatedEvent;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
public class FactoryService extends ModelService<Factory, FactoryRepository> implements ProductionLineService {

    private final ApplicationEventPublisher events;
    private final SaveRepository saveRepository;
    private final IconService iconService;
    private final ItemService itemService;
    private final ProductionStepService productionStepService;
    private final ResourceService resourceService;
    private final Map<Integer, ProductionLine> cache = new HashMap<>();

    public FactoryService(FactoryRepository repository, ApplicationEventPublisher events, SaveRepository saveRepository,
                          IconService iconService, ItemService itemService, ProductionStepService productionStepService,
                          ResourceService resourceService) {
        super(repository);
        this.events = events;
        this.saveRepository = saveRepository;
        this.iconService = iconService;
        this.itemService = itemService;
        this.productionStepService = productionStepService;
        this.resourceService = resourceService;
    }

    public void create(int saveId, FactoryStandalone standalone) {
        Save save = saveRepository.findById(saveId).orElseThrow(ModelService::reportNotFound);
        Factory factory = new Factory(save, standalone);
        applyRelations(factory, standalone);
        factory = create(factory);
        save.getFactories().add(factory);
        saveRepository.save(save);
        events.publishEvent(new FactoryProductionLineChangedEvent(factory, startEmptyProductionLine(factory), false));
    }

    private ProductionLine startEmptyProductionLine(Factory factory) {
        ProductionLine productionLine = new ProductionLine(factory.getId(), this);
        cache.put(factory.getId(), productionLine);
        return productionLine;
    }

    @Override
    public Factory create(Factory entity) {
        if (0 >= entity.getOrdinal()) {
            entity.setOrdinal(
                    entity.getSave().getFactories().stream().mapToInt(Factory::getOrdinal).max().orElse(0) + 1);
        }
        return super.create(entity);
    }

    public ProductionLine
            computeProductionLine(Factory factory,
                                  Function<? super ProductionStep, ? extends QuantityByChangelist> changes) {
        return cache.computeIfAbsent(factory.getId(), key -> initProductionLine(factory, changes));
    }

    private ProductionLine
            initProductionLine(Factory factory,
                               Function<? super ProductionStep, ? extends QuantityByChangelist> changes) {
        ProductionLine productionLine = new ProductionLine(factory.getId(), this);
        for (Resource resource : factory.getResources()) {
            productionLine.addResource(resource);
        }
        for (ProductionStep productionStep : factory.getProductionSteps()) {
            productionLine.addContributor(
                    productionStepService.computeThroughputs(productionStep, () -> changes.apply(productionStep)));
        }
        if (productionLine.hasAlteredResources()) {
            repository.save(factory);
        }
        events.publishEvent(new FactoryProductionLineChangedEvent(factory, productionLine, true));
        return productionLine;
    }

    @Override
    public ResourceContributions spawnResource(int id, int itemId) {
        Factory factory = get(id);
        Resource resource = new Resource();
        resource.setFactory(factory);
        resource.setItem(itemService.get(itemId));
        resource = resourceService.create(resource);
        factory.getResources().add(resource);
        return resourceService.computeContributions(resource);
    }

    @Override
    public void notifyResourceUpdate(int id, ResourceContributions contributions) {
        resourceService.updateContributions(contributions);
    }

    @Override
    public void destroyResource(int id, int resourceId) {
        resourceService.delete(resourceId);
    }

    public FactorySummary getFactorySummary(Factory factory,
                                            Function<? super ProductionStep, ? extends QuantityByChangelist> changes) {
        FactorySummary summary = new FactorySummary();
        summary.setProductionSteps(factory.getProductionSteps().stream()
                .map(productionStep -> ProductionStepStandalone.of(productionStep,
                        productionStepService.computeThroughputs(productionStep, () -> QuantityByChangelist.ZERO)))
                .toList());
        ProductionLine productionLine = computeProductionLine(factory, changes);
        summary.setResources(factory.getResources().stream()
                .map(resource -> ResourceStandalone.of(resource, productionLine.getContributions(resource))).toList());
        summary.setFactory(FactoryStandalone.of(factory, productionLine));
        return summary;
    }

    public void reorder(int saveId, List<EntityPosition> input) {
        Save save = saveRepository.findById(saveId).orElseThrow(ModelService::reportNotFound);
        Map<Integer, Integer> order = input.stream().collect(toMap(EntityPosition::id, EntityPosition::ordinal));
        Collection<Factory> factories = new ArrayList<>();
        for (Factory factory : save.getFactories()) {
            Integer ordinal = order.get(factory.getId());
            if (null != ordinal) {
                factory.setOrdinal(ordinal.intValue());
                factories.add(factory);
                repository.save(factory);
            }
        }
        events.publishEvent(new FactoriesReorderedEvent(save.getId(), factories));
    }

    public void update(int id, FactoryStandalone standalone) {
        Factory factory = get(id);
        factory.applyBasics(standalone);
        applyRelations(factory, standalone);
        update(factory);
    }

    private void applyRelations(Factory factory, FactoryStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(factory::setIcon);
    }

    @Override
    public Factory update(Factory entity) {
        // no need to invalidate resources, since we don't change anything related
        Factory factory = super.update(entity);
        events.publishEvent(new FactoryUpdatedEvent(factory));
        return factory;
    }

    @Override
    public void delete(int id) {
        Save save = saveRepository.findByFactoriesId(id);
        if (null == save) {
            throw report(HttpStatus.CONFLICT, "factory does not belong to a save");
        }
        if (1 == repository.countBySaveId(save.getId())) {
            throw report(HttpStatus.CONFLICT, "cannot delete the last factory of a save");
        }
        super.delete(id);
        ProductionLine productionLine = cache.remove(id);
        events.publishEvent(new FactoryRemovedEvent(save.getId(), id, productionLine));
    }

    @EventListener
    public FactoryProductionLineChangedEvent on(ProductionStepThroughputsChangedEvent event) {
        ProductionLine productionLine = cache.get(event.getProductionStep().getFactory().getId());
        if (null == productionLine) {
            return null;
        }
        boolean itemsChanged = false;
        if (event.isItemsChanged()) {
            productionLine.updateContribution(event.getThroughputs());
            itemsChanged = productionLine.hasAlteredResources();
            if (itemsChanged) {
                repository.save(event.getProductionStep().getFactory());
            }
        } else {
            productionLine.updateContributor(event.getThroughputs());
        }
        return new FactoryProductionLineChangedEvent(event.getProductionStep().getFactory(), productionLine,
                itemsChanged);
    }

    @EventListener
    public void on(ProductionStepRemovedEvent event) {
        ProductionLine productionLine = cache.get(event.getFactoryId());
        if (null != productionLine) {
            if (null != event.getThroughputs()) {
                productionLine.removeContributor(event.getThroughputs());
                events.publishEvent(
                        new FactoryProductionLineChangedEvent(get(event.getFactoryId()), productionLine, true));
            } else {
                // TODO find a better solution
                // we don't have access to the old throughputs here, so we can only do a full invalidate
                cache.remove(event.getFactoryId());
            }
        }
    }

    @EventListener
    public FactoryProductionLineChangedEvent on(ResourceUpdatedEvent event) {
        if (event.isComplete()) {
            // in that case, the ensuing event is already being propagated otherwise
            return null;
        }
        ProductionLine productionLine = cache.get(event.getResource().getFactory().getId());
        if (null == productionLine) {
            return null;
        }
        productionLine.updateResource(event.getResource());
        // TODO we might want to distinguish between the Factory ProductionLine and the Factory Production
        return new FactoryProductionLineChangedEvent(event.getResource().getFactory(), productionLine, false);
    }

}
