package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.engine.Changelists;
import de.yggdrasil128.factorial.engine.ProductionLineResources;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepRemoved;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepThroughputsChanged;
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
                                          Supplier<? extends Changelists> changelists) {
        factory.getProductionSteps().add(productionStep);
        repository.save(factory);
        ProductionLineResources resources = cache.get(factory.getId());
        if (null != resources) {
            resources.addContributor(productionStepService.computeThroughputs(productionStep, changelists));
        }
    }

    public ProductionLineResources computeResources(Factory factory, Supplier<? extends Changelists> changelists) {
        return cache.computeIfAbsent(factory.getId(), key -> initProductionLineResources(factory, changelists));
    }

    private ProductionLineResources initProductionLineResources(Factory factory,
                                                                Supplier<? extends Changelists> changelists) {
        ProductionLineResources resources = new ProductionLineResources(itemId -> spawnResource(factory, itemId),
                this::fireResourceUpdated, resourceService::delete);
        for (Resource resource : factory.getResources()) {
            resources.addResource(resource);
        }
        for (ProductionStep productionStep : factory.getProductionSteps()) {
            resources.addContributor(productionStepService.computeThroughputs(productionStep, changelists));
        }
        return resources;
    }

    private ResourceContributions spawnResource(Factory factory, int itemId) {
        return resourceService.spawn(factory, itemService.get(itemId));
    }

    private void fireResourceUpdated(int resourceId, ResourceContributions contributions) {
        events.publishEvent(new ResourceContributionsChanged(resourceService.get(resourceId), contributions));
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
    public void on(ProductionStepThroughputsChanged event) {
        ProductionLineResources resources = cache.get(event.getProductionStep().getFactory().getId());
        if (null != resources) {
            if (event.isRecipeChanged()) {
                resources.removeContributor(event.getThroughputs());
                resources.addContributor(event.getThroughputs());
            } else {
                resources.updateContributor(event.getThroughputs());
            }
        }
    }

    @EventListener
    public void on(ProductionStepRemoved event) {
        if (null != event.getThroughputs()) {
            ProductionLineResources resources = cache.get(event.getFactoryId());
            if (null != resources) {
                resources.removeContributor(event.getThroughputs());
            }
        }
    }

}
