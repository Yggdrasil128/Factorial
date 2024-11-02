package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.engine.ProductionLine;

public class FactoryRemovedEvent {

    private final int saveId;
    private final int factoryId;
    private final ProductionLine productionLine;

    public FactoryRemovedEvent(int saveId, int factoryId, ProductionLine productionLine) {
        this.saveId = saveId;
        this.factoryId = factoryId;
        this.productionLine = productionLine;
    }

    public int getSaveId() {
        return saveId;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public ProductionLine getProductionLine() {
        return productionLine;
    }

}
