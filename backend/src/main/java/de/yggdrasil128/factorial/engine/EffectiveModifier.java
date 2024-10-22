package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;

public class EffectiveModifier {

    public static EffectiveModifier neutral() {
        return new EffectiveModifier(Fraction.ONE, Fraction.ONE, Fraction.ONE);
    }

    public static EffectiveModifier of(RecipeModifier recipeModifier) {
        return new EffectiveModifier(recipeModifier.getDurationMultiplier(),
                recipeModifier.getInputQuantityMultiplier(), recipeModifier.getOutputQuantityMultiplier());
    }

    private final Fraction durationMultiplier;
    private final Fraction inputQuantityMultiplier;
    private final Fraction outputQuantityMultiplier;

    private EffectiveModifier(Fraction durationMultiplier, Fraction inputQuantityMultiplier,
                              Fraction outputQuantityMultiplier) {
        this.durationMultiplier = durationMultiplier;
        this.inputQuantityMultiplier = inputQuantityMultiplier;
        this.outputQuantityMultiplier = outputQuantityMultiplier;
    }

    public EffectiveModifier multiply(RecipeModifier modifier) {
        return new EffectiveModifier(durationMultiplier.multiply(modifier.getDurationMultiplier()),
                inputQuantityMultiplier.multiply(modifier.getInputQuantityMultiplier()),
                outputQuantityMultiplier.multiply(modifier.getOutputQuantityMultiplier()));
    }

    public EffectiveModifier multiplyQuantity(Fraction value) {
        return new EffectiveModifier(durationMultiplier, inputQuantityMultiplier.multiply(value),
                outputQuantityMultiplier.multiply(value));
    }

    public Fraction getDurationMultiplier() {
        return durationMultiplier;
    }

    public Fraction getInputQuantityMultiplier() {
        return inputQuantityMultiplier;
    }

    public Fraction getOutputQuantityMultiplier() {
        return outputQuantityMultiplier;
    }

    public Fraction getInputSpeedMultiplier() {
        return inputQuantityMultiplier.divide(durationMultiplier);
    }

    public Fraction getOutputSpeedMultiplier() {
        return outputQuantityMultiplier.divide(durationMultiplier);
    }

    @Override
    public String toString() {
        return "duration x " + durationMultiplier + " | input x " + inputQuantityMultiplier + " | output x "
                + outputQuantityMultiplier;
    }

}
