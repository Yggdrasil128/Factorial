package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.QuantityByChangelist;

public class ProductionStepChangelistEntryChanged {

    private final int productionStepId;
    private final QuantityByChangelist changes;

    public ProductionStepChangelistEntryChanged(int productionStepId, QuantityByChangelist changes) {
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
