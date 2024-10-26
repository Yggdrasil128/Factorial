package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.QuantityByChangelist;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.ChangelistProductionStepChangeAppliedEvent;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
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

    public ProductionStepThroughputs computeThroughputs(ProductionStep productionStep,
                                                        Supplier<? extends ProductionStepChanges> changes) {
        return cache.computeIfAbsent(productionStep.getId(), key -> initThroughputs(productionStep, changes));
    }

    private ProductionStepThroughputs computeThroughputs(ProductionStep productionStep,
                                                         Supplier<? extends ProductionStepChanges> changes,
                                                         Consumer<? super ProductionStepThroughputs> update) {
        return cache.compute(productionStep.getId(), (key, existing) -> {
            if (null == existing) {
                return initThroughputs(productionStep, changes);
            }
            update.accept(existing);
            return existing;
        });
    }

    private static ProductionStepThroughputs initThroughputs(ProductionStep productionStep,
                                                             Supplier<? extends ProductionStepChanges> changes) {
        return new ProductionStepThroughputs(productionStep, changes.get().getChanges(productionStep.getId()));
    }

    @Override
    public ProductionStep update(ProductionStep entity) {
        // TODO revise class hierarchy
        throw new UnsupportedOperationException("use the class-local overload");
    }

    // TODO reduce this method to only name/icon and make individual methods for recipe, machine and machine count
    public ProductionStep update(ProductionStep entity, ProductionStepStandalone before,
                                 Supplier<? extends ProductionStepChanges> changes) {
        ProductionStep productionStep = super.update(entity);
        /*
         * If the recipe has changed, we do a hard invalidate, because ProductionLineResources has to remove and add the
         * throughputs object anyway. Otherwise we do a soft invalidate, hence we try to keep the same object and update
         * it if needed.
         */
        if ((int) before.getRecipe() != productionStep.getRecipe().getId()) {
            ProductionStepThroughputs throughputs = initThroughputs(productionStep, changes);
            // hard invalidate cache in this case
            cache.put(productionStep.getId(), throughputs);
            events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, true));
        } else {
            ProductionStepThroughputs throughputs = computeThroughputs(productionStep, changes,
                    existing -> existing.update(productionStep));
            events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, false));
        }
        return productionStep;
    }

    public void setCurrentMachineCount(ProductionStep productionStep, Fraction value, ProductionStepChanges changes) {
        productionStep.setMachineCount(value);
        repository.save(productionStep);
        ProductionStepThroughputs throughputs = computeThroughputs(productionStep, () -> changes,
                existing -> existing.updateMachineCount(productionStep, productionStep.getMachineCount()));
        events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, false));
    }

    public void handleChangelistEntryChanged(ProductionStep productionStep, ProductionStepChanges changes) {
        QuantityByChangelist machineCountChanges = changes.getChanges(productionStep.getId());
        ProductionStepThroughputs throughputs = cache.compute(productionStep.getId(), (key, oldValue) -> {
            if (null == oldValue) {
                return new ProductionStepThroughputs(productionStep, machineCountChanges);
            }
            oldValue.updateMachineCounts(productionStep, machineCountChanges);
            return oldValue;
        });
        events.publishEvent(new ProductionStepThroughputsChangedEvent(productionStep, throughputs, false));
    }

    @Override
    public void delete(int id) {
        Factory factory = factories.findByResourcesId(id);
        super.delete(id);
        ProductionStepThroughputs throughputs = cache.remove(id);
        events.publishEvent(new ProductionStepRemovedEvent(factory.getSave().getId(), factory.getId(), id, throughputs));
    }

    @EventListener
    public ProductionStepUpdatedEvent on(ProductionStepChangelistEntryChangedEvent event) {
        ProductionStepThroughputs throughputs = cache.get(event.getProductionStepId());
        ProductionStep productionStep = get(event.getProductionStepId());
        if (null == throughputs) {
            return new ProductionStepUpdatedEvent(productionStep, false);
        }
        throughputs.updateMachineCounts(productionStep, event.getChanges());
        return new ProductionStepThroughputsChangedEvent(productionStep, throughputs, false);
    }

    @EventListener
    public void on(ChangelistProductionStepChangeAppliedEvent event) {
        setCurrentMachineCount(event.getProductionStep(),
                event.getProductionStep().getMachineCount().add(event.getChange()), event.getChanges());
    }

}
