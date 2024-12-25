package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.model.save.SaveRelatedEntityRemovedEvent;

public class FactoryRemovedEvent extends SaveRelatedEntityRemovedEvent {

    private final int factoryId;
    private final ProductionLine productionLine;

    public FactoryRemovedEvent(int saveId, int factoryId, ProductionLine productionLine) {
        super(saveId);
        this.factoryId = factoryId;
        this.productionLine = productionLine;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public ProductionLine getProductionLine() {
        return productionLine;
    }

}
