package de.yggdrasil128.factorial.scraper.satis;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;

import static java.util.Collections.emptyList;

public record FactorialRecipeModifier(String name,
                                      String description,
                                      Fraction durationMultiplier,
                                      Fraction inputQuantityMultiplier,
                                      Fraction outputQuantityMultiplier) {

    public RecipeModifierStandalone toStandalone() {
        return new RecipeModifierStandalone(0, 0, name, description, null, durationMultiplier, inputQuantityMultiplier,
                outputQuantityMultiplier, emptyList());
    }

}
