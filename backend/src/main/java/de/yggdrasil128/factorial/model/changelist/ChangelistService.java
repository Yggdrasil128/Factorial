package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.engine.Changelists;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.ReorderInputEntry;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.save.Save;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

@Service
public class ChangelistService extends ModelService<Changelist, ChangelistRepository> {

    private final ProductionStepService productionSteps;

    public ChangelistService(ChangelistRepository repository, ProductionStepService productionSteps) {
        super(repository);
        this.productionSteps = productionSteps;
    }

    @Override
    public Changelist create(Changelist changelist) {
        Save save = changelist.getSave();
        if (0 < changelist.getOrdinal()) {
            changelist.setOrdinal(save.getChangelists().stream().mapToInt(Changelist::getOrdinal).max().orElse(0) + 1);
        }
        Changelist result = super.create(changelist);
        if (result.isPrimary()) {
            Changelists.getOptionalPrimary(save).ifPresent(primary -> {
                primary.setPrimary(false);
                repository.save(primary);
            });
        }
        return result;
    }

    public static Changelist createSentinel(Save save) {
        return new Changelist(save, 1, "Default", true, true, null, emptyMap());
    }

    public List<Factory> apply(int id) {
        Changelist changelist = get(id);
        Set<Factory> affectedFactories = new HashSet<>();
        for (Map.Entry<ProductionStep, Fraction> entry : changelist.getProductionStepChanges().entrySet()) {
            productionSteps.applyChange(entry.getKey(), entry.getValue());
            affectedFactories.add(entry.getKey().getFactory());
        }
        changelist.getProductionStepChanges().clear();
        repository.save(changelist);
        return new ArrayList<>(affectedFactories);
    }

    public void setPrimary(int id) {
        Changelist changelist = get(id);
        if (!changelist.isPrimary()) {
            Changelist primary = Changelists.getPrimary(changelist.getSave());
            changelist.setPrimary(true);
            changelist.setActive(true);
            repository.save(changelist);
            primary.setPrimary(false);
            repository.save(primary);
        }
    }

    public void setActive(int id, boolean active) {
        Changelist changelist = get(id);
        if (!changelist.isPrimary()) {
            if (changelist.isActive() != active) {
                changelist.setActive(active);
                repository.save(changelist);
            }
        } else if (!active) {
            throw report(HttpStatus.CONFLICT, "cannot set primary changelist to inactive");
        }
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

    public void reportMachineCount(Changelist changelist, ProductionStep productionStep, Fraction machineCount) {
        Fraction delta = machineCount.subtract(productionStep.getMachineCount());
        if (Fraction.ZERO.equals(delta)) {
            changelist.getProductionStepChanges().remove(productionStep);
        } else {
            changelist.getProductionStepChanges().put(productionStep, delta);
        }
        repository.save(changelist);
    }

    @Override
    public void delete(int id) {
        if (repository.existsByIdAndPrimaryIsTrue(id)) {
            throw report(HttpStatus.CONFLICT, "cannot delete primary changelist");
        }
        super.delete(id);
    }

}
