package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.engine.ProductionLineResources;
import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.productionstep.*;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceContributionsChanged;
import de.yggdrasil128.factorial.model.resource.ResourceService;
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
public class FactoryService extends ModelService<Factory, FactoryRepository> {

    private final ApplicationEventPublisher events;
    private final SaveRepository saves;
    private final ItemService itemService;
    private final ProductionStepService productionStepService;
    private final ResourceService resourceService;
    private final Map<Integer, ProductionLineResources> cache = new HashMap<>();

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
        events.publishEvent(new ProductionStepThroughputsChanged(productionStep, throughputs, true));
    }

    public ProductionLineResources computeResources(Factory factory,
                                                    Supplier<? extends ProductionStepChanges> changes) {
        return cache.computeIfAbsent(factory.getId(), key -> initProductionLineResources(factory, changes));
    }

    private ProductionLineResources initProductionLineResources(Factory factory,
                                                                Supplier<? extends ProductionStepChanges> changes) {
        ProductionLineResources resources = new ProductionLineResources(itemId -> spawnResource(factory, itemId),
                this::fireResourceUpdated, resourceService::delete);
        for (Resource resource : factory.getResources()) {
            resources.addResource(resource);
        }
        for (ProductionStep productionStep : factory.getProductionSteps()) {
            resources.addContributor(productionStepService.computeThroughputs(productionStep, changes));
        }
        return resources;
    }

    private ResourceContributions spawnResource(Factory factory, int itemId) {
        return resourceService.spawn(factory, itemService.get(itemId));
    }

    private void fireResourceUpdated(ResourceContributions contributions) {
        events.publishEvent(
                new ResourceContributionsChanged(resourceService.get(contributions.getResourceId()), contributions));
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
        return super.update(entity);
    }

    @Override
    public void delete(int id) {
        Save save = saves.findByFactoriesId(id);
        if (null != save && 1 == repository.countBySaveId(save.getId())) {
            throw report(HttpStatus.CONFLICT, "cannot delete the last factory of a save");
        }
        super.delete(id);
        cache.remove(id);
    }

    @EventListener
    public void on(ProductionStepUpdated event) {
        ProductionLineResources resources = cache.get(event.getProductionStep().getFactory().getId());
        if (null != resources) {
            if (event instanceof ProductionStepThroughputsChanged throughputEvent) {
                if (event.isRecipeChanged()) {
                    resources.removeContributor(throughputEvent.getThroughputs());
                    resources.addContributor(throughputEvent.getThroughputs());
                } else {
                    resources.updateContributor(throughputEvent.getThroughputs());
                }
            } else {
                // TODO find a better solution
                // we don't have access to the new throughputs here, so we can only do a full invalidate
                cache.remove(event.getProductionStep().getFactory().getId());
            }
        }
    }

    @EventListener
    public void on(ProductionStepRemoved event) {
        ProductionLineResources resources = cache.get(event.getFactoryId());
        if (null != resources) {
            if (null != event.getThroughputs()) {
                resources.removeContributor(event.getThroughputs());
            } else {
                // TODO find a better solution
                // we don't have access to the old throughputs here, so we can only do a full invalidate
                cache.remove(event.getFactoryId());
            }
        }
    }

}
