package de.yggdrasil128.factorial.model.productionstep;

public class ProductionStepUpdatedEvent {

    private final ProductionStep productionStep;
    private final boolean recipeChanged;

    public ProductionStepUpdatedEvent(ProductionStep productionStep, boolean recipeChanged) {
        this.productionStep = productionStep;
        this.recipeChanged = recipeChanged;
    }

    public ProductionStep getProductionStep() {
        return productionStep;
    }

    public boolean isRecipeChanged() {
        return recipeChanged;
    }

}
