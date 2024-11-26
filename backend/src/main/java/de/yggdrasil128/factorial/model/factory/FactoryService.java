package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.productionstep.*;
import de.yggdrasil128.factorial.model.resource.local.LocalResource;
import de.yggdrasil128.factorial.model.resource.local.LocalResourceService;
import de.yggdrasil128.factorial.model.resource.local.LocalResourceStandalone;
import de.yggdrasil128.factorial.model.resource.local.LocalResourceUpdatedEvent;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
public class FactoryService extends ParentedModelService<Factory, FactoryStandalone, Save, FactoryRepository>
        implements ProductionLineService {

    private static final Logger LOG = LoggerFactory.getLogger(FactoryService.class);

    private final ApplicationEventPublisher events;
    private final SaveRepository saveRepository;
    private final IconService iconService;
    private final ProductionStepService productionStepService;
    private final LocalResourceService resourceService;
    private final Map<Integer, ProductionLine> cache = new HashMap<>();

    public FactoryService(FactoryRepository repository, ApplicationEventPublisher events, SaveRepository saveRepository,
                          IconService iconService, ProductionStepService productionStepService,
                          LocalResourceService resourceService) {
        super(repository);
        this.events = events;
        this.saveRepository = saveRepository;
        this.iconService = iconService;
        this.productionStepService = productionStepService;
        this.resourceService = resourceService;
    }

    @Override
    protected int getEntityId(Factory factory) {
        return factory.getId();
    }

    @Override
    protected int getStandaloneId(FactoryStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Save getParentEntity(int parentId) {
        return saveRepository.findById(parentId).orElseThrow(ModelService::reportNotFound);
    }

    @Override
    protected Factory prepareCreate(Save save, FactoryStandalone standalone) {
        ensureUniqueName(save, standalone);
        Factory factory = new Factory(save, standalone);
        applyRelations(factory, standalone);
        inferOrdinal(save, factory);
        return factory;
    }

    private static void inferOrdinal(Save save, Factory factory) {
        if (0 >= factory.getOrdinal()) {
            factory.setOrdinal(save.getFactories().stream().mapToInt(Factory::getOrdinal).max().orElse(0) + 1);
        }
    }

    @Override
    protected void handleBulkCreate(Save save, Iterable<Factory> factories) {
        for (Factory factory : factories) {
            save.getFactories().add(factory);
            events.publishEvent(
                    new FactoryProductionLineChangedEvent(factory, initEmptyProductionLine(factory), false));
        }
        saveRepository.save(save);
    }

    private ProductionLine initEmptyProductionLine(Factory factory) {
        ProductionLine productionLine = new ProductionLine(factory.getId(), this);
        cache.put(factory.getId(), productionLine);
        return productionLine;
    }

    @Override
    protected void prepareUpdate(Factory factory, FactoryStandalone standalone) {
        ensureUniqueName(factory.getSave(), standalone);
        factory.applyBasics(standalone);
        applyRelations(factory, standalone);
    }

    private void ensureUniqueName(Save save, FactoryStandalone standalone) {
        if (null != standalone.name() && repository.existsBySaveIdAndName(save.getId(), standalone.name())) {
            throw report(HttpStatus.CONFLICT, "A Factory with that name already exists");
        }
    }

    private void applyRelations(Factory factory, FactoryStandalone standalone) {
        OptionalInputField.ofId(standalone.iconId(), iconService::get).apply(factory::setIcon);
    }

    @Override
    protected void handleUpdate(Factory factory) {
        events.publishEvent(new FactoryUpdatedEvent(factory));
    }

    @Override
    protected Save findParentEntity(int id) {
        Save save = saveRepository.findByFactoriesId(id);
        if (null == save) {
            throw report(HttpStatus.CONFLICT, "factory does not belong to a save");
        }
        save.getFactories().remove(get(id));
        return save;
    }

    @Override
    protected void handleDelete(Save save, int id) {
        ProductionLine productionLine = cache.remove(id);
        events.publishEvent(new FactoryRemovedEvent(save.getId(), id, productionLine));
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
        for (LocalResource resource : factory.getResources()) {
            productionLine.addResource(resource, () -> resourceService.computeContributions(resource));
        }
        for (ProductionStep productionStep : factory.getProductionSteps()) {
            productionLine.addContributor(
                    productionStepService.computeThroughputs(productionStep, () -> changes.apply(productionStep)));
        }
        if (productionLine.hasAlteredResources()) {
            repository.save(factory);
        }
        return productionLine;
    }

    @Override
    public ResourceContributions spawnResource(int id, int itemId) {
        return resourceService.spawn(id, itemId);
    }

    @Override
    public void notifyResourceUpdate(int id, ResourceContributions contributions) {
        resourceService.updateContributions(contributions);
    }

    @Override
    public void destroyResource(int id, int resourceId) {
        resourceService.destroy(get(id), resourceId);
    }

    public FactorySummary
            getFrontendFactorySummary(Factory factory, External destination,
                                      Function<? super ProductionStep, ? extends QuantityByChangelist> changes) {
        FactorySummary summary = new FactorySummary();
        summary.setProductionSteps(factory.getProductionSteps().stream()
                .map(productionStep -> ProductionStepStandalone.of(productionStep,
                        productionStepService.computeThroughputs(productionStep, () -> changes.apply(productionStep))))
                .toList());
        ProductionLine productionLine = computeProductionLine(factory, changes);
        summary.setResources(factory.getResources().stream()
                .map(resource -> LocalResourceStandalone.of(resource, productionLine.getContributions(resource)))
                .toList());
        summary.setFactory(FactoryStandalone.of(factory, productionLine));
        return summary;
    }

    @Transactional
    public void reorder(int saveId, List<EntityPosition> input, CompletableFuture<Void> result) {
        Save save = saveRepository.findById(saveId).orElseThrow(ModelService::reportNotFound);
        Map<Integer, Integer> order = input.stream().collect(toMap(EntityPosition::id, EntityPosition::ordinal));
        AsyncHelper.complete(result);
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

    @EventListener
    public FactoryProductionLineChangedEvent on(ProductionStepThroughputsChangedEvent event) {
        Factory factory = event.getProductionStep().getFactory();
        ProductionLine productionLine = cache.get(factory.getId());
        if (null == productionLine) {
            return null;
        }
        boolean itemsChanged = false;
        if (event.isItemsChanged()) {
            productionLine.updateContribution(event.getThroughputs());
            itemsChanged = productionLine.hasAlteredResources();
            if (itemsChanged) {
                repository.save(factory);
            }
        } else {
            productionLine.updateContributor(event.getThroughputs());
        }
        return new FactoryProductionLineChangedEvent(factory, productionLine, itemsChanged);
    }

    @EventListener
    public FactoryProductionLineChangedEvent on(ProductionStepRemovedEvent event) {
        ProductionLine productionLine = cache.get(event.getFactoryId());
        if (null == productionLine) {
            return null;
        }
        if (null != event.getThroughputs()) {
            productionLine.removeContributor(event.getThroughputs());
            return new FactoryProductionLineChangedEvent(get(event.getFactoryId()), productionLine, true);
        }
        // TODO find a better solution
        // we don't have access to the old throughputs here, so we can only do a full invalidate
        LOG.warn(
                "Removed a production step that had no computed throughputs, doing a full invalidate for Production Line of Factory {}",
                event.getFactoryId());
        cache.remove(event.getFactoryId());
        return null;
    }

    @EventListener
    public FactoryProductionLineChangedEvent on(LocalResourceUpdatedEvent event) {
        Factory factory = event.getResource().getFactory();
        if (!event.isImportExportChanged()) {
            /*
             * We are just trying to propagate this to the enclosing save. If import/export has not changed, the
             * enclosing save will be unaffected, so we can just stop here.
             */
            return null;
        }
        ProductionLine productionLine = cache.get(factory.getId());
        if (null == productionLine) {
            // TODO fix this
            LOG.error(
                    "Some Resource import/export was changed, but the Production Line for Factory {} is not computed, aborting event sequence",
                    factory);
            return null;
        }
        // TODO we might want to distinguish between the Factory ProductionLine and the Factory Production
        return new FactoryProductionLineChangedEvent(factory, productionLine, true);
    }

}
