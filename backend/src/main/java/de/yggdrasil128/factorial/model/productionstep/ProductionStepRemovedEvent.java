package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.model.ModelChangedEvent;

public class ProductionStepRemovedEvent implements ModelChangedEvent {

    private final int saveId;
    private final int factoryId;
    private final int productionStepId;
    private final ProductionStepThroughputs throughputs;

    public ProductionStepRemovedEvent(int saveId, int factoryId, int productionStepId,
                                      ProductionStepThroughputs throughputs) {
        this.saveId = saveId;
        this.factoryId = factoryId;
        this.productionStepId = productionStepId;
        this.throughputs = throughputs;
    }

    @Override
    public int getSaveId() {
        return saveId;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public int getProductionStepId() {
        return productionStepId;
    }

    public ProductionStepThroughputs getThroughputs() {
        return throughputs;
    }

}
