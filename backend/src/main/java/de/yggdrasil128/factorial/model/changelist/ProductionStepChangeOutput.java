package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepOutput;

public class ProductionStepChangeOutput extends ProductionStepOutput {

    private final Fraction change;

    public ProductionStepChangeOutput(ProductionStep delegate, Fraction change) {
        super(delegate);
        this.change = change;
    }

    public Fraction getChange() {
        return change;
    }

}