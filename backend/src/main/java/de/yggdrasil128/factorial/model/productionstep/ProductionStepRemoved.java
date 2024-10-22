package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.model.ModelChanged;

public class ProductionStepRemoved implements ModelChanged {

    private final int saveId;
    private final int factoryId;
    private final int productionStepId;
    private final ProductionStepThroughputs throughputs;

    public ProductionStepRemoved(int saveId, int factoryId, int productionStepId,
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
