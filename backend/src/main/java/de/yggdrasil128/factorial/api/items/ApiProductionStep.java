package de.yggdrasil128.factorial.api.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.QuantityByChangelist;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstepchange.ProductionStepChange;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.resource.Resource;

public class ApiProductionStep {

    private final ProductionStep delegate;
    private final List<Throughput> input;
    private final List<Throughput> output;
    private final QuantityByChangelist machineCount;

    public ApiProductionStep(ProductionStep delegate, Changelist primary, Iterable<Changelist> active) {
        this.delegate = delegate;
        this.machineCount = getMachineCounts(delegate, primary, active);

        RecipeModifier effectiveModifier = delegate.getEffectiveModifier();
        RecipeModifier modifierCurrent = getEffectiveCopy(effectiveModifier);
        applyMachineCount(modifierCurrent, machineCount.getCurrent());
        RecipeModifier withPrimaryChangelist = getEffectiveCopy(effectiveModifier);
        applyMachineCount(withPrimaryChangelist, machineCount.getWithPrimaryChangelist());
        RecipeModifier withActiveChangelists = getEffectiveCopy(effectiveModifier);
        applyMachineCount(withActiveChangelists, machineCount.getWithActiveChangelists());

        input = getThroughputs(delegate, delegate.getRecipe().getInput(), RecipeModifier::getInputSpeedMultiplier,
            modifierCurrent, withPrimaryChangelist, withActiveChangelists);
        output = getThroughputs(delegate, delegate.getRecipe().getOutput(), RecipeModifier::getOutputSpeedMultiplier,
            modifierCurrent, withPrimaryChangelist, withActiveChangelists);
    }

    private static RecipeModifier getEffectiveCopy(RecipeModifier effectiveModifier) {
        RecipeModifier copy = new RecipeModifier();
        copy.setInputQuantityMultiplier(effectiveModifier.getInputQuantityMultiplier());
        copy.setOutputQuantityMultiplier(effectiveModifier.getOutputQuantityMultiplier());
        copy.setDurationMultiplier(effectiveModifier.getDurationMultiplier());
        return copy;
    }

    private static QuantityByChangelist getMachineCounts(ProductionStep delegate, Changelist current,
        Iterable<Changelist> active) {
        Fraction withAllChangelists = delegate.getMachineCount();
        Fraction withCurrentChangelist = withAllChangelists.add(getChangeFor(current, delegate));
        for (Changelist changelist : active) {
            withAllChangelists = withAllChangelists.add(getChangeFor(changelist, delegate));
        }
        return new QuantityByChangelist(delegate.getMachineCount(), withCurrentChangelist, withAllChangelists);
    }

    private static Fraction getChangeFor(Changelist changelist, ProductionStep target) {
        for (ProductionStepChange entry : changelist.getProductionStepChanges()) {
            if (entry.getProductionStep().equals(target)) {
                return entry.getChange();
            }
        }
        return Fraction.ZERO;
    }

    private static void applyMachineCount(RecipeModifier effectiveModifier, Fraction machineCount) {
        effectiveModifier
            .setInputQuantityMultiplier(effectiveModifier.getInputQuantityMultiplier().multiply(machineCount));
        effectiveModifier
            .setOutputQuantityMultiplier(effectiveModifier.getOutputQuantityMultiplier().multiply(machineCount));
    }

    private static List<Throughput> getThroughputs(ProductionStep productionStep, List<Resource> resources,
        Function<RecipeModifier, Fraction> speedMultiplier, RecipeModifier current,
        RecipeModifier withPrimaryChangelist, RecipeModifier withActiveChangelists) {
        List<Throughput> throughputs = new ArrayList<>(resources.size());
        for (Resource resource : resources) {
            Fraction baseSpeed = resource.getQuantity().divide(productionStep.getRecipe().getDuration());
            QuantityByChangelist throughput = new QuantityByChangelist(
                baseSpeed.multiply(speedMultiplier.apply(current)),
                baseSpeed.multiply(speedMultiplier.apply(withPrimaryChangelist)),
                baseSpeed.multiply(speedMultiplier.apply(withActiveChangelists)));
            throughputs.add(new Throughput(resource.getItem(), throughput));
        }
        return throughputs;
    }

    public int getMachineId() {
        return delegate.getMachine().getId();
    }

    public int getRecipeId() {
        return delegate.getRecipe().getId();
    }

    public List<Throughput> getInput() {
        return input;
    }

    public List<Throughput> getOutput() {
        return output;
    }

    public QuantityByChangelist getMachineCount() {
        return machineCount;
    }

}
