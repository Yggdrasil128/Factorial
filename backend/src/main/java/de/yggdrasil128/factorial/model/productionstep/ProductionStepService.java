package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.QuantityByChangelist;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class ProductionStepService extends ModelService<ProductionStep, ProductionStepRepository> {

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

    public void create(int factoryId, ProductionStepStandalone standalone) {
        Factory factory = factoryRepository.findById(factoryId).orElseThrow(ModelService::reportNotFound);
        ProductionStep productionStep = new ProductionStep(factory, standalone);
        applyRelations(productionStep, standalone);
        productionStep = super.create(productionStep);
        ProductionStepThroughputs throughputs = new ProductionStepThroughputs(productionStep,
                QuantityByChangelist.ZERO);
        factory.getProductionSteps().add(productionStep);
        factoryRepository.save(factory);
        cache.put(productionStep.getId(), throughputs);
        events.publishEvent(new ProductionStepThroughputsInitalizedEvent(productionStep, throughputs));
    }

    public ProductionStepThroughputs computeThroughputs(ProductionStep productionStep,
                                                        Supplier<? extends QuantityByChangelist> changes) {
        return cache.computeIfAbsent(productionStep.getId(),
                key -> new ProductionStepThroughputs(productionStep, changes.get()));
    }

    public void update(int id, ProductionStepStandalone standalone) {
        ProductionStep productionStep = get(id);
        int recipeId = productionStep.getRecipe().getId();
        productionStep.applyBasics(standalone);
        applyRelations(productionStep, standalone);
        productionStep = super.update(productionStep);
        boolean itemsChanged = recipeId != productionStep.getRecipe().getId();
        ProductionStepThroughputs throughputs = cache.get(productionStep.getId());
        if (null != throughputs) {
            throughputs.update(productionStep);
            events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, itemsChanged));
        } else {
            events.publishEvent(new ProductionStepUpdatedEvent(productionStep, itemsChanged));
        }
    }

    public void setMachineCount(ProductionStep productionStep, ProductionStepThroughputs throughputs, Fraction change) {
        productionStep.setMachineCount(productionStep.getMachineCount().add(change));
        productionStep = update(productionStep);
        events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, false));
    }

    private void applyRelations(ProductionStep productionStep, ProductionStepStandalone standalone) {
        OptionalInputField.ofId(standalone.recipeId(), recipeService::get).apply(productionStep::setRecipe);
        OptionalInputField.ofIds(standalone.modifierIds(), recipeModifierService::get)
                .applyList(productionStep::setModifiers);
        OptionalInputField.ofId(standalone.machineId(), machineService::get).apply(productionStep::setMachine);
    }

    @Override
    public void delete(int id) {
        Factory factory = factoryRepository.findByProductionStepsId(id);
        super.delete(id);
        ProductionStepThroughputs throughputs = cache.remove(id);
        events.publishEvent(
                new ProductionStepRemovedEvent(factory.getSave().getId(), factory.getId(), id, throughputs));
    }

}
