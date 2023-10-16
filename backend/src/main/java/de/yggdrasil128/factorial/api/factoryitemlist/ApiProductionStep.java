package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.QuantityByChangelist;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.machine.MachineOutput;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepOutput;
import de.yggdrasil128.factorial.model.recipe.RecipeOutput;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ApiProductionStep {

    private final ProductionStepOutput delegate;
    private final List<Throughput> input;
    private final List<Throughput> output;
    private final QuantityByChangelist machineCount;

    public ApiProductionStep(ProductionStep delegate, Changelist primary, Iterable<Changelist> active) {
        this.delegate = new ProductionStepOutput(delegate);
        this.machineCount = getMachineCounts(delegate, primary, active);
        /*
         * To save some cycles, we calculate the "internal" effective modifier of the given production step once and
         * then, for each variant of changelist application, make an independent copy and multiply its machine count by
         * the respective value. We then use these modifiers to calculate the throughputs for all inputs and outputs.
         */
        RecipeModifier effectiveModifier = getEffectiveModifier(delegate);
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

    private static RecipeModifier getEffectiveModifier(ProductionStep delegate) {
        RecipeModifier result = new RecipeModifier();
        for (RecipeModifier modifier : delegate.getMachine().getMachineModifiers()) {
            result.setInputQuantityMultiplier(
                    result.getInputQuantityMultiplier().multiply(modifier.getInputQuantityMultiplier()));
            result.setOutputQuantityMultiplier(
                    result.getOutputQuantityMultiplier().multiply(modifier.getOutputQuantityMultiplier()));
            result.setDurationMultiplier(result.getDurationMultiplier().multiply(modifier.getDurationMultiplier()));
        }
        for (RecipeModifier modifier : delegate.getModifiers()) {
            result.setInputQuantityMultiplier(
                    result.getInputQuantityMultiplier().multiply(modifier.getInputQuantityMultiplier()));
            result.setOutputQuantityMultiplier(
                    result.getOutputQuantityMultiplier().multiply(modifier.getOutputQuantityMultiplier()));
            result.setDurationMultiplier(result.getDurationMultiplier().multiply(modifier.getDurationMultiplier()));
        }
        return result;
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
        for (Map.Entry<ProductionStep, Fraction> entry : changelist.getProductionStepChanges().entrySet()) {
            if (entry.getKey().equals(target)) {
                return entry.getValue();
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
                                                   Function<RecipeModifier, Fraction> speedMultiplier,
                                                   RecipeModifier current, RecipeModifier withPrimaryChangelist,
                                                   RecipeModifier withActiveChangelists) {
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

    public int getId() {
        return delegate.getId();
    }

    public int getFactoryId() {
        return delegate.getFactoryId();
    }

    public MachineOutput getMachine() {
        return delegate.getMachine();
    }

    public RecipeOutput getRecipe() {
        return delegate.getRecipe();
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
