package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class ChangelistService extends ModelService<Changelist, ChangelistRepository> {

    private final ApplicationEventPublisher events;
    private final SaveRepository saves;

    public ChangelistService(ChangelistRepository repository, ApplicationEventPublisher events, SaveRepository saves) {
        super(repository);
        this.events = events;
        this.saves = saves;
    }

    @Override
    public Changelist create(Changelist changelist) {
        Save save = changelist.getSave();
        if (0 < changelist.getOrdinal()) {
            changelist.setOrdinal(save.getChangelists().stream().mapToInt(Changelist::getOrdinal).max().orElse(0) + 1);
        }
        return super.create(changelist);
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

    @Override
    public Changelist update(Changelist entity) {
        Changelist changelist = super.update(entity);
        events.publishEvent(new ChangelistUpdatedEvent(changelist, true));
        return changelist;
    }

    public void apply(Changelist changelist, ProductionStepChanges changes) {
        for (Map.Entry<ProductionStep, Fraction> change : changelist.getProductionStepChanges().entrySet()) {
            events.publishEvent(
                    new ChangelistProductionStepChangeAppliedEvent(change.getKey(), change.getValue(), changes));
        }
        changelist.getProductionStepChanges().clear();
        repository.save(changelist);
        events.publishEvent(new ChangelistUpdatedEvent(changelist, false));
    }

    public void reportMachineCount(int changelistId, ProductionStep productionStep, Fraction change) {
        Changelist changelist = get(changelistId);
        if (Fraction.ZERO.equals(change)) {
            changelist.getProductionStepChanges().remove(productionStep);
        } else {
            changelist.getProductionStepChanges().put(productionStep, change);
        }
        repository.save(changelist);
        events.publishEvent(new ChangelistUpdatedEvent(changelist, false));
    }

    @Override
    public void delete(int id) {
        if (repository.existsByIdAndPrimaryIsTrue(id)) {
            throw report(HttpStatus.CONFLICT, "cannot delete primary changelist");
        }
        Save save = saves.findByChangelistsId(id);
        super.delete(id);
        events.publishEvent(new ChangelistRemovedEvent(save.getId(), id));
    }

}
