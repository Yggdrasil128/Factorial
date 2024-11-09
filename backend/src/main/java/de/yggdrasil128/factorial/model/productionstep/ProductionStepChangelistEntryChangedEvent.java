package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.QuantityByChangelist;

public class ProductionStepChangelistEntryChangedEvent {

    private final int productionStepId;
    private final QuantityByChangelist changes;

    public ProductionStepChangelistEntryChangedEvent(int productionStepId, QuantityByChangelist changes) {
        this.productionStepId = productionStepId;
        this.changes = changes;
    }

    public int getProductionStepId() {
        return productionStepId;
    }

    public QuantityByChangelist getChanges() {
        return changes;
    }

}
