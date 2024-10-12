package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.itemQuantity.ItemQuantity;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Throughputs {

    public static Map<Item, QuantityByChangelist> inputOf(ProductionStep productionStep,
                                                          EffectiveModifiers effectiveModifiers) {
        return getThroughputs(productionStep, productionStep.getRecipe().getIngredients(),
                RecipeModifier::getInputSpeedMultiplier, effectiveModifiers);
    }

    public static Map<Item, QuantityByChangelist> outputOf(ProductionStep productionStep,
                                                           EffectiveModifiers effectiveModifiers) {
        return getThroughputs(productionStep, productionStep.getRecipe().getProducts(),
                RecipeModifier::getOutputSpeedMultiplier, effectiveModifiers);
    }

    private static Map<Item, QuantityByChangelist>
            getThroughputs(ProductionStep productionStep, List<ItemQuantity> resources,
                           Function<RecipeModifier, Fraction> speedMultiplier, EffectiveModifiers effectiveModifiers) {
        return resources.stream().collect(Collectors.toMap(ItemQuantity::getItem, resource -> {
            Fraction baseSpeed = resource.getQuantity().divide(productionStep.getRecipe().getDuration());
            return new QuantityByChangelist(baseSpeed.multiply(speedMultiplier.apply(effectiveModifiers.getCurrent())),
                    baseSpeed.multiply(speedMultiplier.apply(effectiveModifiers.getWithPrimaryChangelist())),
                    baseSpeed.multiply(speedMultiplier.apply(effectiveModifiers.getWithActiveChangelists())));
        }));
    }

}
