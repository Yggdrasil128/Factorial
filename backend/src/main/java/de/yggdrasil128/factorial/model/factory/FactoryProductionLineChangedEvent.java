package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.engine.ProductionLine;

public class FactoryProductionLineChangedEvent extends FactoryUpdatedEvent {

    private final ProductionLine productionLine;
    private final boolean itemsChanged;

    public FactoryProductionLineChangedEvent(Factory factory, ProductionLine productionLine, boolean itemsChanged) {
        super(factory);
        this.productionLine = productionLine;
        this.itemsChanged = itemsChanged;
    }

    public ProductionLine getProductionLine() {
        return productionLine;
    }

    public boolean isItemsChanged() {
        return itemsChanged;
    }

}
