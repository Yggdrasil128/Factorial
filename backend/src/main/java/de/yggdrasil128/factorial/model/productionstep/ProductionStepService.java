package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.model.*;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class ProductionStepService
        extends ParentedModelService<ProductionStep, ProductionStepStandalone, Factory, ProductionStepRepository> {

    private final ApplicationEventPublisher events;
    private final FactoryRepository factoryRepository;
    private final RecipeService recipeService;
    private final RecipeModifierService recipeModifierService;
    private final MachineService machineService;
    private final Map<Integer, ProductionStepThroughputs> cache = new HashMap<>();

    public ProductionStepService(ProductionStepRepository repository, ApplicationEventPublisher events,
                                 FactoryRepository factoryRepository, RecipeService recipeService,
                                 RecipeModifierService recipeModifierService, MachineService machineService) {
        super(repository);
        this.events = events;
        this.factoryRepository = factoryRepository;
        this.recipeService = recipeService;
        this.recipeModifierService = recipeModifierService;
        this.machineService = machineService;
    }

    @Override
    protected int getEntityId(ProductionStep productionStep) {
        return productionStep.getId();
    }

    @Override
    protected int getStandaloneId(ProductionStepStandalone standalone) {
        return standalone.id();
    }

    @Override
    protected Factory getParentEntity(int parentId) {
        return factoryRepository.findById(parentId).orElseThrow(ModelService::reportNotFound);
    }

    @Override
    protected ProductionStep prepareCreate(Factory factory, ProductionStepStandalone standalone) {
        ProductionStep productionStep = new ProductionStep(factory, standalone);
        applyRelations(productionStep, standalone);
        return productionStep;
    }

    @Override
    protected void handleBulkCreate(Factory factory, Iterable<ProductionStep> productionSteps) {
        for (ProductionStep productionStep : productionSteps) {
            factory.getProductionSteps().add(productionStep);
            events.publishEvent(
                    new ProductionStepThroughputsInitalizedEvent(productionStep, initThroughputs(productionStep)));
        }
        factoryRepository.save(factory);
    }

    private ProductionStepThroughputs initThroughputs(ProductionStep productionStep) {
        ProductionStepThroughputs throughputs = new ProductionStepThroughputs(productionStep,
                QuantityByChangelist.ZERO);
        cache.put(productionStep.getId(), throughputs);
        return throughputs;
    }

    @Override
    protected void prepareUpdate(ProductionStep productionStep, ProductionStepStandalone standalone) {
        productionStep.applyBasics(standalone);
        applyRelations(productionStep, standalone);
    }

    @Override
    protected void handleUpdate(ProductionStep productionStep) {
        ProductionStepThroughputs throughputs = cache.get(productionStep.getId());
        if (null != throughputs) {
            boolean itemsChanged = throughputs.update(productionStep);
            events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, itemsChanged));
        } else {
            events.publishEvent(new ProductionStepUpdatedEvent(productionStep, true));
        }
    }

    @Override
    protected Factory findParentEntity(int id) {
        Factory factory = factoryRepository.findByProductionStepsId(id);
        if (null == factory) {
            throw report(HttpStatus.CONFLICT, "production step does not belong to a factory");
        }
        factory.getProductionSteps().remove(get(id));
        return factory;
    }

    @Override
    protected void handleDelete(Factory factory, int id) {
        ProductionStepThroughputs throughputs = cache.remove(id);
        events.publishEvent(
                new ProductionStepRemovedEvent(factory.getSave().getId(), factory.getId(), id, throughputs));
    }

    public ProductionStepThroughputs computeThroughputs(ProductionStep productionStep,
                                                        Supplier<? extends QuantityByChangelist> changes) {
        return cache.computeIfAbsent(productionStep.getId(),
                key -> new ProductionStepThroughputs(productionStep, changes.get()));
    }

    public void setMachineCount(ProductionStep productionStep, ProductionStepThroughputs throughputs, Fraction change) {
        productionStep.setMachineCount(productionStep.getMachineCount().add(change));
        productionStep = repository.save(productionStep);
        events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, false));
    }

    private void applyRelations(ProductionStep productionStep, ProductionStepStandalone standalone) {
        OptionalInputField.ofId(standalone.recipeId(), recipeService::get).apply(productionStep::setRecipe);
        OptionalInputField.ofIds(standalone.modifierIds(), recipeModifierService::get)
                .applyList(productionStep::setModifiers);
        OptionalInputField.ofId(standalone.machineId(), machineService::get).apply(productionStep::setMachine);
    }

}
