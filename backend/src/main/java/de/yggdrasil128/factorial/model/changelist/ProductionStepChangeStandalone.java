package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

import java.util.Map;

public class ProductionStepChangeStandalone {

    private Object productionStepId;
    private Fraction change;

    public ProductionStepChangeStandalone() {
    }

    public ProductionStepChangeStandalone(Map.Entry<ProductionStep, Fraction> model,
                                          RelationRepresentation resolveStrategy) {
        productionStepId = ProductionStep.resolve(model.getKey(), resolveStrategy);
        change = model.getValue();
    }

    public Object getProductionStepId() {
        return productionStepId;
    }

    public void setProductionStepId(Object productionStep) {
        this.productionStepId = productionStep;
    }

    public Fraction getChange() {
        return change;
    }

    public void setChange(Fraction change) {
        this.change = change;
    }

}
