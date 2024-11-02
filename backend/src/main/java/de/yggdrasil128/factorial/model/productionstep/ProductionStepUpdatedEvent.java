package de.yggdrasil128.factorial.model.productionstep;

public class ProductionStepUpdatedEvent {

    private final ProductionStep productionStep;
    private final boolean itemsChanged;

    public ProductionStepUpdatedEvent(ProductionStep productionStep, boolean itemsChanged) {
        this.productionStep = productionStep;
        this.itemsChanged = itemsChanged;
    }

    public ProductionStep getProductionStep() {
        return productionStep;
    }

    public boolean isItemsChanged() {
        return itemsChanged;
    }

}
