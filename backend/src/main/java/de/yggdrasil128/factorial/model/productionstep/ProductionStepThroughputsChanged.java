package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;

public class ProductionStepThroughputsChanged extends ProductionStepUpdated {

    private final ProductionStepThroughputs throughputs;
    private final boolean recipeChanged;

    public ProductionStepThroughputsChanged(ProductionStep productionStep, ProductionStepThroughputs throughputs,
                                            boolean recipeChanged) {
        super(productionStep);
        this.throughputs = throughputs;
        this.recipeChanged = recipeChanged;
    }

    public ProductionStepThroughputs getThroughputs() {
        return throughputs;
    }

    public boolean isRecipeChanged() {
        return recipeChanged;
    }

}
