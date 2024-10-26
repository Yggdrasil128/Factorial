package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

public class ChangelistProductionStepChangeApplied {

    private final ProductionStep productionStep;
    private final Fraction change;
    private final ProductionStepChanges changes;

    public ChangelistProductionStepChangeApplied(ProductionStep productionStep, Fraction change,
                                                 ProductionStepChanges changes) {
        this.productionStep = productionStep;
        this.change = change;
        this.changes = changes;
    }

    public ProductionStep getProductionStep() {
        return productionStep;
    }

    public Fraction getChange() {
        return change;
    }

    public ProductionStepChanges getChanges() {
        return changes;
    }

}
