package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;

public class EffectiveModifiers {

    public static EffectiveModifiers of(ProductionStep productionStep, QuantityByChangelist machineCounts) {
        EffectiveModifier base = EffectiveModifier.neutral();
        for (RecipeModifier modifier : productionStep.getMachine().getMachineModifiers()) {
            base = base.multiply(modifier);
        }
        for (RecipeModifier modifier : productionStep.getModifiers()) {
            base = base.multiply(modifier);
        }
        return new EffectiveModifiers(base.multiplyQuantity(machineCounts.getCurrent()),
                base.multiplyQuantity(machineCounts.getWithPrimaryChangelist()),
                base.multiplyQuantity(machineCounts.getWithActiveChangelists()));
    }

    private final EffectiveModifier current;
    private final EffectiveModifier withPrimaryChangelist;
    private final EffectiveModifier withActiveChangelists;

    private EffectiveModifiers(EffectiveModifier current, EffectiveModifier withPrimaryChangelist,
                               EffectiveModifier withActiveChangelists) {
        this.current = current;
        this.withPrimaryChangelist = withPrimaryChangelist;
        this.withActiveChangelists = withActiveChangelists;
    }

    public EffectiveModifier getCurrent() {
        return current;
    }

    public EffectiveModifier getWithPrimaryChangelist() {
        return withPrimaryChangelist;
    }

    public EffectiveModifier getWithActiveChangelists() {
        return withActiveChangelists;
    }

}
