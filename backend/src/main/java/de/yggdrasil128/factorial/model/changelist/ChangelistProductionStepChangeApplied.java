package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

public class ChangelistProductionStepChangeApplied {

    private ProductionStep productionStep;
    private Fraction change;

    public ChangelistProductionStepChangeApplied(ProductionStep productionStep, Fraction change) {
        this.productionStep = productionStep;
        this.change = change;
    }

    public ProductionStep getProductionStep() {
        return productionStep;
    }

    public Fraction getChange() {
        return change;
    }

}
