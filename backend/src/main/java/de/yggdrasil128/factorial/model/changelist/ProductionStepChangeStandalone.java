package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

import java.util.Map;

public class ProductionStepChangeStandalone {

    private Object productionStep;
    private Fraction change;

    public ProductionStepChangeStandalone() {
    }

    public ProductionStepChangeStandalone(Map.Entry<ProductionStep, Fraction> model,
                                          RelationRepresentation resolveStrategy) {
        productionStep = ProductionStep.resolve(model.getKey(), resolveStrategy);
        change = model.getValue();
    }

    public Object getProductionStep() {
        return productionStep;
    }

    public void setProductionStep(Object productionStep) {
        this.productionStep = productionStep;
    }

    public Fraction getChange() {
        return change;
    }

    public void setChange(Fraction change) {
        this.change = change;
    }

}
