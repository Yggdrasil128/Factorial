package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;

public class EffectiveModifiers {

    public static EffectiveModifiers of(ProductionStep productionStep, QuantityByChangelist machineCounts) {
        /*
         * To save some cycles, we calculate the "internal" effective modifier of the given production step once and
         * then, for each variant of changelist application, make an independent copy and multiply its machine count by
         * the respective value. Later, we can use these modifiers to calculate the throughputs for all inputs and
         * outputs.
         */
        RecipeModifier base = calculateBase(productionStep);
        RecipeModifier current = copy(base);
        applyMachineCount(current, machineCounts.getCurrent());
        RecipeModifier withPrimaryChangelist = copy(base);
        applyMachineCount(withPrimaryChangelist, machineCounts.getWithPrimaryChangelist());
        RecipeModifier withActiveChangelists = copy(base);
        applyMachineCount(withActiveChangelists, machineCounts.getWithActiveChangelists());
        return new EffectiveModifiers(current, withPrimaryChangelist, withActiveChangelists);
    }

    private static RecipeModifier calculateBase(ProductionStep delegate) {
        RecipeModifier result = new RecipeModifier();
        for (RecipeModifier modifier : delegate.getMachine().getMachineModifiers()) {
            merge(result, modifier);
        }
        for (RecipeModifier modifier : delegate.getModifiers()) {
            merge(result, modifier);
        }
        return result;
    }

    private static void merge(RecipeModifier sink, RecipeModifier modifier) {
        sink.setInputQuantityMultiplier(
                sink.getInputQuantityMultiplier().multiply(modifier.getInputQuantityMultiplier()));
        sink.setOutputQuantityMultiplier(
                sink.getOutputQuantityMultiplier().multiply(modifier.getOutputQuantityMultiplier()));
        sink.setDurationMultiplier(sink.getDurationMultiplier().multiply(modifier.getDurationMultiplier()));
    }

    private static RecipeModifier copy(RecipeModifier recipeModifier) {
        RecipeModifier copy = new RecipeModifier();
        copy.setInputQuantityMultiplier(recipeModifier.getInputQuantityMultiplier());
        copy.setOutputQuantityMultiplier(recipeModifier.getOutputQuantityMultiplier());
        copy.setDurationMultiplier(recipeModifier.getDurationMultiplier());
        return copy;
    }

    private static void applyMachineCount(RecipeModifier recipeModifier, Fraction machineCount) {
        recipeModifier.setInputQuantityMultiplier(recipeModifier.getInputQuantityMultiplier().multiply(machineCount));
        recipeModifier.setOutputQuantityMultiplier(recipeModifier.getOutputQuantityMultiplier().multiply(machineCount));
    }

    private final RecipeModifier current;
    private final RecipeModifier withPrimaryChangelist;
    private final RecipeModifier withActiveChangelists;

    private EffectiveModifiers(RecipeModifier current, RecipeModifier withPrimaryChangelist,
                               RecipeModifier withActiveChangelists) {
        this.current = current;
        this.withPrimaryChangelist = withPrimaryChangelist;
        this.withActiveChangelists = withActiveChangelists;
    }

    public RecipeModifier getCurrent() {
        return current;
    }

    public RecipeModifier getWithPrimaryChangelist() {
        return withPrimaryChangelist;
    }

    public RecipeModifier getWithActiveChangelists() {
        return withActiveChangelists;
    }

}
