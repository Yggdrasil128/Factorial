package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

import java.util.stream.Stream;

/**
 * Represents a set of {@link EffectiveModifier effective modifiers} for a {@link ProductionStep} depending on primary
 * and active {@link Changelist Changelists}.
 */
public class EffectiveModifiers {

    private EffectiveModifier fromMachine;
    private EffectiveModifier fromProductionStep;
    private QuantityByChangelist machineCounts;
    private EffectiveModifier current;
    private EffectiveModifier primary;
    private EffectiveModifier active;

    public EffectiveModifiers(ProductionStep productionStep, QuantityByChangelist machineCounts) {
        fromMachine = computeEffectiveMachineModifier(productionStep.getMachine());
        fromProductionStep = computeEffecitveProductionStepModifier(productionStep);
        this.machineCounts = machineCounts;
        recompute();
    }

    public void applyMachine(Machine source) {
        fromMachine = computeEffectiveMachineModifier(source);
        recompute();
    }

    public void applyProductionStep(ProductionStep source) {
        fromProductionStep = computeEffecitveProductionStepModifier(source);
        recompute();
    }

    public boolean applyMachineCount(Fraction value) {
        if (machineCounts.getCurrent().equals(value)) {
            return false;
        }
        machineCounts = new QuantityByChangelist(value, machineCounts.getWithPrimaryChangelist(),
                machineCounts.getWithActiveChangelists());
        recompute();
        return true;
    }

    public boolean applyMachineCounts(QuantityByChangelist value) {
        if (machineCounts.equals(value)) {
            return false;
        }
        machineCounts = value;
        recompute();
        return true;
    }

    public boolean changeMachineCounts(QuantityByChangelist change) {
        if (change.isZero()) {
            return false;
        }
        machineCounts = machineCounts.add(change);
        recompute();
        return true;
    }

    private static EffectiveModifier computeEffectiveMachineModifier(Machine machine) {
        return EffectiveModifier.multiply(machine.getMachineModifiers().stream().map(EffectiveModifier::of));
    }

    private static EffectiveModifier computeEffecitveProductionStepModifier(ProductionStep productionStep) {
        return EffectiveModifier.multiply(productionStep.getModifiers().stream().map(EffectiveModifier::of));
    }

    private void recompute() {
        EffectiveModifier base = EffectiveModifier.multiply(Stream.of(fromMachine, fromProductionStep));
        current = base.multiplyQuantity(machineCounts.getCurrent());
        primary = base.multiplyQuantity(machineCounts.getWithPrimaryChangelist());
        active = base.multiplyQuantity(machineCounts.getWithActiveChangelists());
    }

    public QuantityByChangelist getMachineCounts() {
        return machineCounts;
    }

    public EffectiveModifier getCurrent() {
        return current;
    }

    public EffectiveModifier getPrimary() {
        return primary;
    }

    public EffectiveModifier getActive() {
        return active;
    }

    @Override
    public String toString() {
        return "[current=" + current + ", primary=" + primary + ", active=" + active + "]";
    }

}
