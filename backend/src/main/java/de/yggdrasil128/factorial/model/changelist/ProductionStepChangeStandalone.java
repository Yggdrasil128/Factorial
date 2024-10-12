package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.save.Save;

import java.util.Map;

public class ProductionStepChangeStandalone {

    private Object productionStep;
    private Fraction change;

    public ProductionStepChangeStandalone() {
    }

    public ProductionStepChangeStandalone(Map.Entry<ProductionStep, Fraction> model,
                                          RelationRepresentation resolveStrategy) {
        productionStep = resolve(model.getKey(), resolveStrategy);
        change = model.getValue();
    }

    private static Object resolve(ProductionStep relation, RelationRepresentation strategy) {
        switch (strategy) {
        case ID:
            return relation.getId();
        case NAME:
            Factory factory = relation.getFactory();
            Save save = factory.getSave();
            return save.getFactories().indexOf(factory) + "." + factory.getProductionSteps().indexOf(relation);
        default:
            throw new AssertionError("unexpected enum constant: " + RelationRepresentation.class.getCanonicalName()
                    + '.' + strategy.name());
        }
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
