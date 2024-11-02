package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.ProductionStepChanges;
import de.yggdrasil128.factorial.engine.QuantityByChangelist;

public class ProductionStepChangelistEntryChangedEvent {

    private final int productionStepId;
    private final ProductionStepChanges productionStepChanges;
    private final QuantityByChangelist changes;

    public ProductionStepChangelistEntryChangedEvent(int productionStepId, ProductionStepChanges productionStepChanges,
                                                     QuantityByChangelist changes) {
        this.productionStepId = productionStepId;
        this.productionStepChanges = productionStepChanges;
        this.changes = changes;
    }

    public int getProductionStepId() {
        return productionStepId;
    }

    public ProductionStepChanges getProductionStepChanges() {
        return productionStepChanges;
    }

    public QuantityByChangelist getChanges() {
        return changes;
    }

}
