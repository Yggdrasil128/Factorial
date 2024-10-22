package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.Changelists;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.QuantityByChangelist;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistProductionStepChangeApplied;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class ProductionStepService extends ModelService<ProductionStep, ProductionStepRepository> {

    private final ApplicationEventPublisher events;
    private final FactoryRepository factories;
    private final Map<Integer, ProductionStepThroughputs> cache = new HashMap<>();

    public ProductionStepService(ProductionStepRepository repository, ApplicationEventPublisher events,
                                 FactoryRepository factories) {
        super(repository);
        this.events = events;
        this.factories = factories;
    }

    @Override
    public ProductionStep create(ProductionStep entity) {
        ProductionStep productionStep = super.create(entity);
        events.publishEvent(new ProductionStepUpdated(productionStep));
        return productionStep;
    }

    public ProductionStepThroughputs computeThroughputs(ProductionStep productionStep,
                                                        Supplier<? extends Changelists> changelists) {
        return cache.computeIfAbsent(productionStep.getId(), key -> initThroughputs(productionStep, changelists));
    }

    private static ProductionStepThroughputs initThroughputs(ProductionStep productionStep,
                                                             Supplier<? extends Changelists> changelists) {
        return new ProductionStepThroughputs(productionStep, changelists.get());
    }

    @Override
    public ProductionStep update(ProductionStep entity) {
        // TODO revise class hierarchy
        throw new UnsupportedOperationException("use the class-local overload");
    }

    public ProductionStep update(ProductionStep entity, ProductionStepStandalone before,
                                 Supplier<? extends Changelists> changelists) {
        ProductionStep productionStep = super.update(entity);
        if ((int) before.getRecipe() != productionStep.getRecipe().getId()) {
            ProductionStepThroughputs throughputs = initThroughputs(productionStep, changelists);
            // hard invalidate cache in this case
            cache.put(productionStep.getId(), throughputs);
            events.publishEvent(new ProductionStepThroughputsChanged(productionStep, throughputs, true));
        } else if (before.getMachineCount().equals(productionStep.getMachineCount())) {
            handleCurrentMachineCountChange(productionStep, before.getMachineCount(), productionStep.getMachineCount());
            // no event (for now), this is handled internally
        }
        // TODO handle machine change
        return productionStep;
    }

    public void applyPrimaryChangelist(ProductionStep productionStep, Changelist primary) {
        setCurrentMachineCount(productionStep,
                productionStep.getMachineCount().add(primary.getProductionStepChanges().get(productionStep)));
    }

    public void setCurrentMachineCount(ProductionStep productionStep, Fraction value) {
        Fraction oldValue = productionStep.getMachineCount();
        productionStep.setMachineCount(value);
        repository.save(productionStep);
        handleCurrentMachineCountChange(productionStep, oldValue, value);
    }

    private void handleCurrentMachineCountChange(ProductionStep productionStep, Fraction oldValue, Fraction newValue) {
        applyGlobalThroughputsChange(productionStep,
                amounts -> new QuantityByChangelist(amounts.getCurrent().divide(oldValue).multiply(newValue),
                        amounts.getWithPrimaryChangelist(), amounts.getWithActiveChangelists()));
    }

    public void applyChangelistMachineCountChange(ProductionStep productionStep, boolean primary, Fraction oldValue,
                                                  Fraction newValue) {
        applyGlobalThroughputsChange(productionStep,
                amounts -> new QuantityByChangelist(amounts.getCurrent(),
                        primary ? amounts.getWithPrimaryChangelist().divide(oldValue).multiply(newValue)
                                : amounts.getWithActiveChangelists(),
                        amounts.getWithActiveChangelists().divide(oldValue).multiply(newValue)));
    }

    private void applyGlobalThroughputsChange(ProductionStep productionStep,
                                              Function<QuantityByChangelist, QuantityByChangelist> change) {
        ProductionStepThroughputs throughputs = cache.get(productionStep.getId());
        if (null != throughputs) {
            throughputs.applyGlobalChange(change);
        }
        events.publishEvent(new ProductionStepUpdated(productionStep));
    }

    @Override
    public void delete(int id) {
        Factory factory = factories.findByResourcesId(id);
        super.delete(id);
        cache.remove(id);
        events.publishEvent(new ProductionStepRemoved(factory.getSave().getId(), factory.getId(), id, cache.get(id)));
    }

    @EventListener
    public void on(ChangelistProductionStepChangeApplied event) {
        setCurrentMachineCount(event.getProductionStep(),
                event.getProductionStep().getMachineCount().add(event.getChange()));
    }

}
