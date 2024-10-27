package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.save.Save;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProductionStepChanges {

    private record ChangelistInfo(boolean primary, boolean active, Map<Integer, Fraction> changes) {

        ChangelistInfo(Changelist changelist) {
            this(changelist.isPrimary(), changelist.isActive(), changelist.getProductionStepChanges().entrySet()
                    .stream().collect(Collectors.toMap(entry -> entry.getKey().getId(), Map.Entry::getValue)));
        }

    }

    // key is Changelist.id, but we must not keep references to the entities here
    private final Map<Integer, ChangelistInfo> changelists = new HashMap<>();

    public ProductionStepChanges(Save save) {
        for (Changelist changelist : save.getChangelists()) {
            updateChangelist(changelist);
        }
    }

    public int getPrimaryChangelistId() {
        return changelists.entrySet().stream().filter(entry -> entry.getValue().primary())
                .mapToInt(entry -> entry.getKey().intValue()).findAny()
                .orElseThrow(() -> new IllegalStateException("assertion failed: no primary changelist available"));
    }

    public int[] getActiveChangelistIds() {
        return changelists.entrySet().stream().filter(entry -> entry.getValue().active())
                .mapToInt(entry -> entry.getKey().intValue()).toArray();
    }

    public void updateChangelist(Changelist changelist) {
        changelists.put(changelist.getId(), new ChangelistInfo(changelist));
    }

    public Map<Integer, QuantityByChangelist> removeChangelist(int changelistId) {
        return getChangesAffectedBy(changelists.remove(changelistId));
    }

    public boolean contains(int productionStepId) {
        return changelists.values().stream()
                .flatMapToInt(changelist -> changelist.changes().keySet().stream().mapToInt(Integer::intValue))
                .anyMatch(i -> i == productionStepId);
    }

    public Map<Integer, QuantityByChangelist> getChangesAffectedBy(int changelistId) {
        return getChangesAffectedBy(changelists.get(changelistId));
    }

    private Map<Integer, QuantityByChangelist> getChangesAffectedBy(ChangelistInfo changelist) {
        return changelist.changes().keySet().stream().collect(Collectors.toMap(Function.identity(), this::getChanges));
    }

    public QuantityByChangelist getChanges(int productionStepId) {
        Fraction current = Fraction.ZERO;
        Fraction primary = Fraction.ZERO;
        Fraction active = Fraction.ZERO;
        for (ChangelistInfo changelist : changelists.values()) {
            if (changelist.primary()) {
                primary = applyChange(productionStepId, primary, changelist);
            }
            if (changelist.active()) {
                active = applyChange(productionStepId, active, changelist);
            }
        }
        return new QuantityByChangelist(current, primary, active);
    }

    private static Fraction applyChange(int productionStepId, Fraction current, ChangelistInfo changelist) {
        Fraction change = changelist.changes().get(productionStepId);
        return null == change ? current : current.add(change);
    }

}