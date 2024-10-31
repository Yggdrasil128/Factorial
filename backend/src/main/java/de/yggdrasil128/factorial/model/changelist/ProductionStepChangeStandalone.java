package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

import java.util.Map;

public record ProductionStepChangeStandalone(Object productionStepId, Fraction change) {

    public static ProductionStepChangeStandalone of(Map.Entry<ProductionStep, Fraction> model,
                                                    External destination) {
        return new ProductionStepChangeStandalone(ProductionStep.resolve(model.getKey(), destination),
                model.getValue());
    }

}
