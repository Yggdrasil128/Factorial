package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.save.SaveRelatedEvent;

public class ProductionStepUpdatedEvent implements SaveRelatedEvent {

    private final ProductionStep productionStep;
    private final boolean itemsChanged;

    public ProductionStepUpdatedEvent(ProductionStep productionStep, boolean itemsChanged) {
        this.productionStep = productionStep;
        this.itemsChanged = itemsChanged;
    }

    @Override
    public int getSaveId() {
        return productionStep.getFactory().getSave().getId();
    }

    public ProductionStep getProductionStep() {
        return productionStep;
    }

    public boolean isItemsChanged() {
        return itemsChanged;
    }

}
