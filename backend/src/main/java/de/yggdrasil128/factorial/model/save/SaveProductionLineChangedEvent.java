package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.engine.ProductionLine;

public class SaveProductionLineChangedEvent extends SaveUpdatedEvent {

    private final ProductionLine productionLine;
    private final boolean itemsChanged;

    public SaveProductionLineChangedEvent(Save save, ProductionLine productionLine, boolean itemsChanged) {
        super(save);
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
