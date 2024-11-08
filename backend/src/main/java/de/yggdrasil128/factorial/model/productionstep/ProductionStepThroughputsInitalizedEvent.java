package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;

public class ProductionStepThroughputsInitalizedEvent {

    private final ProductionStep productionStep;
    private final ProductionStepThroughputs throughputs;

    public ProductionStepThroughputsInitalizedEvent(ProductionStep productionStep,
                                                  ProductionStepThroughputs throughputs) {
        this.productionStep = productionStep;
        this.throughputs = throughputs;
    }

    public ProductionStep getProductionStep() {
        return productionStep;
    }

    public ProductionStepThroughputs getThroughputs() {
        return throughputs;
    }

}
