package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.engine.QuantityByChangelist;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.changelist.ChangelistRemovedEvent;
import de.yggdrasil128.factorial.model.changelist.ChangelistUpdatedEvent;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryProductionLineChangedEvent;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepChangelistEntryChangedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Service
public class SaveService extends ModelService<Save, SaveRepository> {

    private final ApplicationEventPublisher events;
    private final FactoryService factoryService;
    private final Map<Integer, ProductionStepChanges> cache = new HashMap<>();

    public SaveService(SaveRepository repository, ApplicationEventPublisher events, FactoryService factoryService) {
        super(repository);
        this.events = events;
        this.factoryService = factoryService;
    }

    public ProductionStepChanges computeProductionStepChanges(Save save) {
        return cache.computeIfAbsent(save.getId(), key -> new ProductionStepChanges(save));
    }

    private ProductionStepChanges computeProductionStepChanges(Save save,
                                                               Consumer<? super ProductionStepChanges> update) {
        return cache.compute(save.getId(), (key, existing) -> {
            if (null == existing) {
                return new ProductionStepChanges(save);
            }
            update.accept(existing);
            return existing;
        });
    }

    public void addAttachedFactory(Save save, Factory factory) {
        save.getFactories().add(factory);
        repository.save(save);
        ProductionLine productionLine = factoryService.computeProductionLine(factory,
                () -> computeProductionStepChanges(save));
        events.publishEvent(new FactoryProductionLineChangedEvent(factory, productionLine, true));
    }

    public void addAttachedChangelist(Save save, Changelist changelist) {
        save.getChangelists().add(changelist);
        repository.save(save);
        events.publishEvent(new ChangelistUpdatedEvent(changelist, true));
    }

    @Override
    public Save update(Save entity) {
        // no need to invalidate resources, since we don't change anything related
        return super.update(entity);
    }

    @Override
    public void delete(int id) {
        super.delete(id);
        cache.remove(id);
    }

    @EventListener
    public Collection<ProductionStepChangelistEntryChangedEvent> on(ChangelistUpdatedEvent event) {
        Changelist changelist = event.getChangelist();
        Save save = changelist.getSave();
        ProductionStepChanges changes = computeProductionStepChanges(save,
                existing -> existing.updateChangelist(changelist));
        return event.isUpdateProductionSteps()
                ? propagateProductionStepChanges(changes, changes.getChangesAffectedBy(changelist.getId()))
                : Collections.emptyList();
    }

    @EventListener
    public Collection<ProductionStepChangelistEntryChangedEvent> on(ChangelistRemovedEvent event) {
        AtomicReference<Map<Integer, QuantityByChangelist>> ref = new AtomicReference<>();
        ProductionStepChanges changes = computeProductionStepChanges(get(event.getSaveId()),
                existing -> ref.set(existing.removeChangelist(event.getChangelistId())));
        return propagateProductionStepChanges(changes, ref.get());
    }

    private static Collection<ProductionStepChangelistEntryChangedEvent>
            propagateProductionStepChanges(ProductionStepChanges changes,
                                           Map<Integer, QuantityByChangelist> affectedProductionSteps) {
        return affectedProductionSteps.entrySet().stream()
                .map(entry -> new ProductionStepChangelistEntryChangedEvent(entry.getKey(), changes, entry.getValue()))
                .toList();
    }

}
