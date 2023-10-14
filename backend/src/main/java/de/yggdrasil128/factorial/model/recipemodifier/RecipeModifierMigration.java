package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.Fraction;

public class RecipeModifierMigration {

    private String description;
    private String iconName;
    private Fraction durationMultiplier;
    private Fraction inputQuantityMultiplier;
    private Fraction outputQuantityMultiplier;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public Fraction getDurationMultiplier() {
        return durationMultiplier;
    }

    public void setDurationMultiplier(Fraction durationMultiplier) {
        this.durationMultiplier = durationMultiplier;
    }

    public Fraction getInputQuantityMultiplier() {
        return inputQuantityMultiplier;
    }

    public void setInputQuantityMultiplier(Fraction inputQuantityMultiplier) {
        this.inputQuantityMultiplier = inputQuantityMultiplier;
    }

    public Fraction getOutputQuantityMultiplier() {
        return outputQuantityMultiplier;
    }

    public void setOutputQuantityMultiplier(Fraction outputQuantityMultiplier) {
        this.outputQuantityMultiplier = outputQuantityMultiplier;
    }

}
