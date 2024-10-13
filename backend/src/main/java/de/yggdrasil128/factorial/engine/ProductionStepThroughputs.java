package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.itemQuantity.ItemQuantity;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductionStepThroughputs implements Production {

    private final ProductionStep delegate;
    private final EffectiveModifiers effectiveModifiers;
    private final Map<Item, QuantityByChangelist> inputs;
    private final Map<Item, QuantityByChangelist> outputs;

    public ProductionStepThroughputs(ProductionStep delegate, Changelists changelists) {
        this.delegate = delegate;
        effectiveModifiers = EffectiveModifiers.of(delegate, MachineCounts.of(delegate, changelists));
        inputs = delegate.getRecipe().getIngredients().stream()
                .collect(Collectors.toMap(ItemQuantity::getItem, ingredient -> new QuantityByChangelist(
                        computeInputRate(ingredient.getQuantity(), effectiveModifiers.getCurrent()),
                        computeInputRate(ingredient.getQuantity(), effectiveModifiers.getWithPrimaryChangelist()),
                        computeInputRate(ingredient.getQuantity(), effectiveModifiers.getWithActiveChangelists())),
                        QuantityByChangelist::add, LinkedHashMap::new));
        outputs = delegate.getRecipe().getProducts().stream().collect(Collectors.toMap(ItemQuantity::getItem,
                product -> new QuantityByChangelist(
                        computeOutputRate(product.getQuantity(), effectiveModifiers.getCurrent()),
                        computeOutputRate(product.getQuantity(), effectiveModifiers.getWithPrimaryChangelist()),
                        computeOutputRate(product.getQuantity(), effectiveModifiers.getWithActiveChangelists())),
                QuantityByChangelist::add, LinkedHashMap::new));
    }

    public ProductionStep getDelegate() {
        return delegate;
    }

    private Fraction computeInputRate(Fraction ingredient, EffectiveModifier effectiveModifier) {
        return ingredient.divide(delegate.getRecipe().getDuration())
                .multiply(effectiveModifier.getInputSpeedMultiplier());
    }

    private Fraction computeOutputRate(Fraction ingredient, EffectiveModifier effectiveModifier) {
        return ingredient.divide(delegate.getRecipe().getDuration())
                .multiply(effectiveModifier.getOutputSpeedMultiplier());
    }

    @Override
    public Map<Item, QuantityByChangelist> getInputs() {
        return inputs;
    }

    @Override
    public Map<Item, QuantityByChangelist> getOutputs() {
        return outputs;
    }

}
