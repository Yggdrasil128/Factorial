package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.Save;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
public class ChangelistService extends ModelService<Changelist, ChangelistRepository> {

    private final ApplicationEventPublisher events;

    public ChangelistService(ChangelistRepository repository, ApplicationEventPublisher events) {
        super(repository);
        this.events = events;
    }

    @Override
    public Changelist create(Changelist entity) {
        Save save = entity.getSave();
        if (0 < entity.getOrdinal()) {
            entity.setOrdinal(save.getChangelists().stream().mapToInt(Changelist::getOrdinal).max().orElse(0) + 1);
        }
        Changelist changelist = super.create(entity);
        events.publishEvent(new ChangelistUpdated(changelist, false));
        return changelist;
    }

    public void reorder(Save save, List<ReorderInputEntry> input) {
        Map<Integer, Integer> order = input.stream()
                .collect(toMap(ReorderInputEntry::getId, ReorderInputEntry::getOrdinal));
        for (Changelist changelist : save.getChangelists()) {
            Integer ordinal = order.get(changelist.getId());
            if (null != ordinal) {
                changelist.setOrdinal(ordinal.intValue());
                repository.save(changelist);
            }
        }
    }

    public void apply(int id) {
        Changelist changelist = get(id);
        for (Map.Entry<ProductionStep, Fraction> change : changelist.getProductionStepChanges().entrySet()) {
            events.publishEvent(new ChangelistProductionStepChangeApplied(change.getKey(), change.getValue()));
        }
        changelist.getProductionStepChanges().clear();
        repository.save(changelist);
        events.publishEvent(new ChangelistUpdated(changelist, false));
    }

    public void makePrimary(int id, Function<? super Changelist, ? extends Changelist> primary) {
        Changelist changelist = get(id);
        if (!changelist.isPrimary()) {
            Changelist oldPrimary = primary.apply(changelist);
            changelist.setPrimary(true);
            changelist.setActive(true);
            repository.save(changelist);
            events.publishEvent(new ChangelistUpdated(changelist, true));
            oldPrimary.setPrimary(false);
            repository.save(oldPrimary);
            events.publishEvent(new ChangelistUpdated(oldPrimary, true));
        }
    }

    public void setActive(int id, boolean active) {
        Changelist changelist = get(id);
        if (!changelist.isPrimary()) {
            changelist.setActive(active);
            repository.save(changelist);
            events.publishEvent(new ChangelistUpdated(changelist, true));
        } else if (!active) {
            throw report(HttpStatus.CONFLICT, "cannot set primary changelist to inactive");
        }
    }

    public void reportMachineCount(Changelist changelist, ProductionStep productionStep, Fraction change) {
        if (Fraction.ZERO.equals(change)) {
            changelist.getProductionStepChanges().remove(productionStep);
        } else {
            changelist.getProductionStepChanges().put(productionStep, change);
        }
        repository.save(changelist);
        events.publishEvent(new ChangelistUpdated(changelist, false));
    }

    @Override
    public void delete(int id) {
        if (repository.existsByIdAndPrimaryIsTrue(id)) {
            throw report(HttpStatus.CONFLICT, "cannot delete primary changelist");
        }
        // TOOD find save id (look into spring projections)
        int saveId = 0;
        super.delete(id);
        events.publishEvent(new ChangelistRemoved(saveId, id));
    }

}
