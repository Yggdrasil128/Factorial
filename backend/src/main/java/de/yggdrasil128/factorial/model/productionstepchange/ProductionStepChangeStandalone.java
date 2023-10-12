package de.yggdrasil128.factorial.model.productionstepchange;

import de.yggdrasil128.factorial.model.Fraction;

public class ProductionStepChangeStandalone {

    private int productionStepId;
    private Fraction change;

    public ProductionStepChangeStandalone() {
    }

    public int getProductionStepId() {
        return productionStepId;
    }

    public void setProductionStepId(int productionStepId) {
        this.productionStepId = productionStepId;
    }

    public Fraction getChange() {
        return change;
    }

    public void setChange(Fraction change) {
        this.change = change;
    }

}
