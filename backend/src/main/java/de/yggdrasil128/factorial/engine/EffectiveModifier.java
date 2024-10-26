package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import jakarta.persistence.Entity;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Represents the combined effect of several {@link RecipeModifier RecipeModifiers}.
 * <p>
 * Usually, we compute a single {@code EffectiveModifier} for an {@link Entity} that maintains multiple
 * {@link RecipeModifier RecipeModifiers}.
 * <p>
 * Instances of this class are immutable.
 */
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

    public static EffectiveModifier multiply(Stream<EffectiveModifier> modifiers) {
        Fraction durationMultiplier = Fraction.ONE;
        Fraction inputQuantityMultiplier = Fraction.ONE;
        Fraction outputQuantityMultiplier = Fraction.ONE;
        for (Iterator<EffectiveModifier> iterator = modifiers.iterator(); iterator.hasNext();) {
            EffectiveModifier modifier = iterator.next();
            durationMultiplier = durationMultiplier.multiply(modifier.getDurationMultiplier());
            inputQuantityMultiplier = inputQuantityMultiplier.multiply(modifier.getInputQuantityMultiplier());
            outputQuantityMultiplier = outputQuantityMultiplier.multiply(modifier.getOutputQuantityMultiplier());
        }
        return new EffectiveModifier(durationMultiplier, inputQuantityMultiplier, outputQuantityMultiplier);
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
