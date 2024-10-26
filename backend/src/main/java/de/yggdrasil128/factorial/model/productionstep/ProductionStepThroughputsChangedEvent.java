package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;

public class ProductionStepThroughputsChangedEvent extends ProductionStepUpdatedEvent {

    private final ProductionStepThroughputs throughputs;

    public ProductionStepThroughputsChangedEvent(ProductionStep productionStep, ProductionStepThroughputs throughputs,
                                                 boolean recipeChanged) {
        super(productionStep, recipeChanged);
        this.throughputs = throughputs;
    }

    public ProductionStepThroughputs getThroughputs() {
        return throughputs;
    }

}
