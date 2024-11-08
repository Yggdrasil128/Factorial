package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.QuantityByChangelist;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.recipe.ItemQuantity;
import de.yggdrasil128.factorial.model.recipe.Recipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * Computes the inputs and outputs of a {@link ProductionStep}, e.g. its {@link Production}.
 * <p>
 * To be kept up-to-date, this implementation must be notified about meaningful changes to the production step, namely
 * <ul>
 * <li>{@link #update(ProductionStep)} for checking everything at once</li>
 * <li>{@link #updateMachineCount(ProductionStep, Fraction)} for its local machine count</li>
 * <li>{@link #updateMachineCounts(ProductionStep, QuantityByChangelist)} for changes to a changelist entry for it</li>
 * </ul
 * <p>
 * The resulting inputs and outputs are cached, hence invoking {@link #getInputs()} or {@link #getOutputs()} is cheap.
 */
public class ProductionStepThroughputs implements Production {

    private record ItemAmount(int itemId, Fraction amount) {

        ItemAmount(ItemQuantity source) {
            this(source.getItem().getId(), source.getQuantity());
        }

    }

    // we must not keep a reference to the entity here
    private final int productionStepId;
    private final EffectiveModifiers effectiveModifiers;

    private List<ItemAmount> ingredients;
    private List<ItemAmount> products;
    private Fraction recipeDuration;

    // key is Item.id, but we must not keep references to the entities here
    private Map<Integer, QuantityByChangelist> inputs;
    // key is Item.id, but we must not keep references to the entities here
    private Map<Integer, QuantityByChangelist> outputs;

    public ProductionStepThroughputs(ProductionStep productionStep, QuantityByChangelist changes) {
        productionStepId = productionStep.getId();
        effectiveModifiers = new EffectiveModifiers(productionStep, machineCounts(productionStep, changes));
        copyRecipeInfo(productionStep.getRecipe());
        recompute();
    }

    public int getProductionStepId() {
        return productionStepId;
    }

    public void update(ProductionStep productionStep) {
        effectiveModifiers.applyMachine(productionStep.getMachine());
        effectiveModifiers.applyProductionStep(productionStep);
        effectiveModifiers.applyMachineCount(productionStep.getMachineCount());
        copyRecipeInfo(productionStep.getRecipe());
        recompute();
    }

    private void copyRecipeInfo(Recipe recipe) {
        ingredients = recipe.getIngredients().stream().map(ItemAmount::new).toList();
        products = recipe.getProducts().stream().map(ItemAmount::new).toList();
        recipeDuration = recipe.getDuration();
    }

    public void updateMachineCount(ProductionStep productionStep, Fraction machineCount) {
        if (effectiveModifiers.applyMachineCount(machineCount)) {
            recompute();
        }
    }

    public void updateMachineCounts(ProductionStep productionStep, QuantityByChangelist machineCounts) {
        if (effectiveModifiers.applyMachineCounts(machineCounts(productionStep, machineCounts))) {
            recompute();
        }
    }

    private static QuantityByChangelist machineCounts(ProductionStep productionStep, QuantityByChangelist changes) {
        return changes.add(productionStep.getMachineCount());
    }

    public void changeMachineCounts(QuantityByChangelist change) {
        if (effectiveModifiers.changeMachineCounts(change)) {
            recompute();
        }
    }

    private void recompute() {
        inputs = computeThroughputs(ingredients, EffectiveModifier::getInputSpeedMultiplier);
        outputs = computeThroughputs(products, EffectiveModifier::getOutputSpeedMultiplier);
    }

    private Map<Integer, QuantityByChangelist>
            computeThroughputs(List<ItemAmount> source, Function<? super EffectiveModifier, ? extends Fraction> speed) {
        return source.stream()
                .collect(toMap(ItemAmount::itemId,
                        itemAmonut -> new QuantityByChangelist(
                                computeRate(itemAmonut.amount(), speed.apply(effectiveModifiers.getCurrent())),
                                computeRate(itemAmonut.amount(), speed.apply(effectiveModifiers.getPrimary())),
                                computeRate(itemAmonut.amount(), speed.apply(effectiveModifiers.getActive()))),
                        QuantityByChangelist::add, LinkedHashMap::new));
    }

    private Fraction computeRate(Fraction amount, Fraction speed) {
        return amount.divide(recipeDuration).multiply(speed);
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

    public QuantityByChangelist getMachineCounts() {
        return effectiveModifiers.getMachineCounts();
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
