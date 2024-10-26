package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;

public class ProductionStepThroughputsChanged extends ProductionStepUpdated {

    private final ProductionStepThroughputs throughputs;

    public ProductionStepThroughputsChanged(ProductionStep productionStep, ProductionStepThroughputs throughputs,
                                            boolean recipeChanged) {
        super(productionStep, recipeChanged);
        this.throughputs = throughputs;
    }

    public ProductionStepThroughputs getThroughputs() {
        return throughputs;
    }

}
