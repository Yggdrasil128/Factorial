package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.ModelChanged;

public class ProductionStepUpdated implements ModelChanged {

    private final ProductionStep productionStep;
    private final boolean recipeChanged;

    public ProductionStepUpdated(ProductionStep productionStep, boolean recipeChanged) {
        this.productionStep = productionStep;
        this.recipeChanged = recipeChanged;
    }

    @Override
    public int getSaveId() {
        return getProductionStep().getFactory().getSave().getId();
    }

    public ProductionStep getProductionStep() {
        return productionStep;
    }

    public boolean isRecipeChanged() {
        return recipeChanged;
    }

}
