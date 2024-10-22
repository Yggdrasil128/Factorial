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
    private final EffectiveModifier primary;
    private final EffectiveModifier active;

    private EffectiveModifiers(EffectiveModifier current, EffectiveModifier rimary, EffectiveModifier ctive) {
        this.current = current;
        this.primary = rimary;
        this.active = ctive;
    }

    public EffectiveModifier getCurrent() {
        return current;
    }

    public EffectiveModifier getPrimary() {
        return primary;
    }

    public EffectiveModifier getActive() {
        return active;
    }

    @Override
    public String toString() {
        return "[current=" + current + ", primary=" + primary + ", active=" + active + "]";
    }

}
