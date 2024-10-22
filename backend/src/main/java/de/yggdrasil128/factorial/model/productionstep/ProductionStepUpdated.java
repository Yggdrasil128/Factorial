package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.ModelChanged;

public class ProductionStepUpdated implements ModelChanged {

    private final ProductionStep productionStep;

    public ProductionStepUpdated(ProductionStep productionStep) {
        this.productionStep = productionStep;
    }

    @Override
    public int getSaveId() {
        return getProductionStep().getFactory().getSave().getId();
    }

    public ProductionStep getProductionStep() {
        return productionStep;
    }

}
