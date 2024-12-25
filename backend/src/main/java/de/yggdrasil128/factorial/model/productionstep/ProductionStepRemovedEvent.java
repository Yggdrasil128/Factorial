package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.model.save.SaveRelatedEntityRemovedEvent;

public class ProductionStepRemovedEvent extends SaveRelatedEntityRemovedEvent {

    private final int factoryId;
    private final int productionStepId;
    private final ProductionStepThroughputs throughputs;

    public ProductionStepRemovedEvent(int saveId, int factoryId, int productionStepId,
                                      ProductionStepThroughputs throughputs) {
        super(saveId);
        this.factoryId = factoryId;
        this.productionStepId = productionStepId;
        this.throughputs = throughputs;
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
