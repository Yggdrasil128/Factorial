package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.ProductionLineService;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepRemovedEvent;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepThroughputsChangedEvent;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceService;
import de.yggdrasil128.factorial.model.resource.ResourceUpdatedEvent;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;

@Service
public class FactoryService extends ModelService<Factory, FactoryRepository> implements ProductionLineService {

    private final ApplicationEventPublisher events;
    private final SaveRepository saves;
    private final ItemService itemService;
    private final ProductionStepService productionStepService;
    private final ResourceService resourceService;
    private final Map<Integer, ProductionLine> cache = new HashMap<>();

    public FactoryService(FactoryRepository repository, ApplicationEventPublisher events, SaveRepository saves,
                          ItemService itemService, ProductionStepService productionStepService,
                          ResourceService resourceService) {
        super(repository);
        this.events = events;
        this.saves = saves;
        this.itemService = itemService;
        this.productionStepService = productionStepService;
        this.resourceService = resourceService;
    }

    @Override
    public Factory create(Factory entity) {
        if (0 >= entity.getOrdinal()) {
            entity.setOrdinal(
                    entity.getSave().getFactories().stream().mapToInt(Factory::getOrdinal).max().orElse(0) + 1);
        }
        return super.create(entity);
    }

    public void addAttachedProductionStep(Factory factory, ProductionStep productionStep,
                                          Supplier<? extends ProductionStepChanges> changes) {
        factory.getProductionSteps().add(productionStep);
        repository.save(factory);
        ProductionStepThroughputs throughputs = productionStepService.computeThroughputs(productionStep, changes);
        events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, true));
    }

    public ProductionLine computeProductionLine(Factory factory, Supplier<? extends ProductionStepChanges> changes) {
        return cache.computeIfAbsent(factory.getId(), key -> initProductionLine(factory, changes));
    }

    private ProductionLine initProductionLine(Factory factory, Supplier<? extends ProductionStepChanges> changes) {
        ProductionLine productionLine = new ProductionLine(factory.getId(), this);
        for (Resource resource : factory.getResources()) {
            productionLine.addResource(resource);
        }
        for (ProductionStep productionStep : factory.getProductionSteps()) {
            productionLine.addContributor(productionStepService.computeThroughputs(productionStep, changes));
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
        resourceService.updateContributions(id, contributions);
    }

    @Override
    public void destroyResource(int id, int resourceId) {
        resourceService.delete(resourceId);
    }

    public void reorder(Save save, List<ReorderInputEntry> input) {
        Map<Integer, Integer> order = input.stream()
                .collect(toMap(ReorderInputEntry::getId, ReorderInputEntry::getOrdinal));
        for (Factory factory : save.getFactories()) {
            Integer ordinal = order.get(factory.getId());
            if (null != ordinal) {
                factory.setOrdinal(ordinal.intValue());
                repository.save(factory);
            }
        }
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
        Save save = saves.findByFactoriesId(id);
        if (null != save && 1 == repository.countBySaveId(save.getId())) {
            throw report(HttpStatus.CONFLICT, "cannot delete the last factory of a save");
        }
        super.delete(id);
        ProductionLine productionLine = cache.remove(id);
        if (null != save) {
            events.publishEvent(new FactoryRemovedEvent(save.getId(), id, productionLine));
        }
    }

    @EventListener
    public FactoryProductionLineChangedEvent on(ProductionStepThroughputsChangedEvent event) {
        ProductionLine productionLine = cache.get(event.getProductionStep().getFactory().getId());
        if (null == productionLine) {
            // TODO guarantee that a production line is computed?
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
                // TODO do we need to save the factory if a resource gets removed?
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
        ProductionLine productionLine = cache.get(event.getResource().getFactory().getId());
        if (null == productionLine) {
            // TODO guarantee that a production line is computed?
            return null;
        }
        productionLine.updateResource(event.getResource());
        // TODO we might want to distinguish between the ProductionLine and the Production
        return new FactoryProductionLineChangedEvent(event.getResource().getFactory(), productionLine, false);
    }

}
