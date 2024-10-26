package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.ModelChangedEvent;

public class ProductionStepUpdatedEvent implements ModelChangedEvent {

    private final ProductionStep productionStep;
    private final boolean recipeChanged;

    public ProductionStepUpdatedEvent(ProductionStep productionStep, boolean recipeChanged) {
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
