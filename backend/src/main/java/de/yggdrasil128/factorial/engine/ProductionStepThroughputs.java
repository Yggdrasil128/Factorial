package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProductionStepThroughputs implements Production {

    // we must not keep a reference to the entity here
    private final int productionStepId;
    private final EffectiveModifiers effectiveModifiers;
    // key is Item.id, but we must not keep references to the entities here
    private final Map<Integer, QuantityByChangelist> inputs;
    // key is Item.id, but we must not keep references to the entities here
    private final Map<Integer, QuantityByChangelist> outputs;

    public ProductionStepThroughputs(ProductionStep delegate, Changelists changelists) {
        productionStepId = delegate.getId();
        effectiveModifiers = EffectiveModifiers.of(delegate, machineCounts(delegate, changelists));
        inputs = delegate.getRecipe().getIngredients().stream()
                .collect(Collectors.toMap(Iingredient -> Iingredient.getItem().getId(),
                        ingredient -> new QuantityByChangelist(
                                computeInputRate(delegate, ingredient.getQuantity(), effectiveModifiers.getCurrent()),
                                computeInputRate(delegate, ingredient.getQuantity(), effectiveModifiers.getPrimary()),
                                computeInputRate(delegate, ingredient.getQuantity(), effectiveModifiers.getActive())),
                        QuantityByChangelist::add, LinkedHashMap::new));
        outputs = delegate.getRecipe().getProducts().stream()
                .collect(Collectors.toMap(product -> product.getItem().getId(),
                        product -> new QuantityByChangelist(
                                computeOutputRate(delegate, product.getQuantity(), effectiveModifiers.getCurrent()),
                                computeOutputRate(delegate, product.getQuantity(), effectiveModifiers.getPrimary()),
                                computeOutputRate(delegate, product.getQuantity(), effectiveModifiers.getActive())),
                        QuantityByChangelist::add, LinkedHashMap::new));
    }

    private static QuantityByChangelist machineCounts(ProductionStep productionStep, Changelists changelists) {
        Fraction active = productionStep.getMachineCount();
        Fraction primary = active.add(getChangeFor(changelists.getPrimary(), productionStep));
        for (Changelist changelist : changelists.getActive()) {
            active = active.add(getChangeFor(changelist, productionStep));
        }
        return new QuantityByChangelist(productionStep.getMachineCount(), primary, active);
    }

    private static Fraction getChangeFor(Changelist changelist, ProductionStep productionStep) {
        return changelist.getProductionStepChanges().getOrDefault(productionStep, Fraction.ZERO);
    }

    private static Fraction computeInputRate(ProductionStep delegate, Fraction ingredient,
                                             EffectiveModifier effectiveModifier) {
        return ingredient.divide(delegate.getRecipe().getDuration())
                .multiply(effectiveModifier.getInputSpeedMultiplier());
    }

    private static Fraction computeOutputRate(ProductionStep delegate, Fraction product,
                                              EffectiveModifier effectiveModifier) {
        return product.divide(delegate.getRecipe().getDuration())
                .multiply(effectiveModifier.getOutputSpeedMultiplier());
    }

    public int getProductionStepId() {
        return productionStepId;
    }

    public void applyPrimaryMachineCount() {
        for (Map.Entry<Integer, QuantityByChangelist> input : inputs.entrySet()) {
            input.setValue(applyPrimaryChangelist(input.getValue()));
        }
        for (Map.Entry<Integer, QuantityByChangelist> output : inputs.entrySet()) {
            output.setValue(applyPrimaryChangelist(output.getValue()));
        }
    }

    private static QuantityByChangelist applyPrimaryChangelist(QuantityByChangelist current) {
        return new QuantityByChangelist(current.getWithPrimaryChangelist(), current.getWithPrimaryChangelist(),
                current.getWithActiveChangelists());
    }

    public void applyGlobalChange(Function<? super QuantityByChangelist, ? extends QuantityByChangelist> change) {
        for (Map.Entry<Integer, QuantityByChangelist> input : inputs.entrySet()) {
            applyChange(input, change);
        }
        for (Map.Entry<Integer, QuantityByChangelist> output : outputs.entrySet()) {
            applyChange(output, change);
        }
    }

    private static void applyChange(Map.Entry<Integer, QuantityByChangelist> entry,
                                    Function<? super QuantityByChangelist, ? extends QuantityByChangelist> change) {
        entry.setValue(change.apply(entry.getValue()));
    }

    @Override
    public Map<Integer, QuantityByChangelist> getInputs() {
        return inputs;
    }

    @Override
    public Map<Integer, QuantityByChangelist> getOutputs() {
        return outputs;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProductionStepThroughputs
                && productionStepId == ((ProductionStepThroughputs) obj).productionStepId;
    }

    @Override
    public int hashCode() {
        return 31 + Integer.hashCode(productionStepId);
    }

    @Override
    public String toString() {
        return "[productionStepId=" + productionStepId + ", inputs=" + inputs + ", outputs=" + outputs + "]";
    }

}
