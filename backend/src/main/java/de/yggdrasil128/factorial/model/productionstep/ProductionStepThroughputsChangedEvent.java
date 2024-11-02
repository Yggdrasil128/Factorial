package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;

public class ProductionStepThroughputsChangedEvent extends ProductionStepUpdatedEvent {

    private final ProductionStepThroughputs throughputs;

    public ProductionStepThroughputsChangedEvent(ProductionStep productionStep, ProductionStepThroughputs throughputs,
                                                 boolean itemsChanged) {
        super(productionStep, itemsChanged);
        this.throughputs = throughputs;
    }

    public ProductionStepThroughputs getThroughputs() {
        return throughputs;
    }

}
