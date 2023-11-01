package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;

public class ProductionStepChangeMigration {

    private int productionStepIndex;
    private Fraction change;

    public int getProductionStepIndex() {
        return productionStepIndex;
    }

    public void setProductionStepIndex(int productionStepIndex) {
        this.productionStepIndex = productionStepIndex;
    }

    public Fraction getChange() {
        return change;
    }

    public void setChange(Fraction change) {
        this.change = change;
    }

}
