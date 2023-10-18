package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

public class MachineCounts {

    public static QuantityByChangelist of(ProductionStep productionStep, Changelists changelists) {
        Fraction withActiveChangelists = productionStep.getMachineCount();
        Fraction withPrimaryChangelist = withActiveChangelists
                .add(getChangeFor(changelists.getPrimary(), productionStep));
        for (Changelist changelist : changelists.getActive()) {
            withActiveChangelists = withActiveChangelists.add(getChangeFor(changelist, productionStep));
        }
        return new QuantityByChangelist(productionStep.getMachineCount(), withPrimaryChangelist, withActiveChangelists);
    }

    private static Fraction getChangeFor(Changelist changelist, ProductionStep productionStep) {
        return changelist.getProductionStepChanges().getOrDefault(productionStep, Fraction.ZERO);
    }

}
