package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.icon.IconOutput;

public class RecipeModifierOutput {

    private final RecipeModifier delegate;
    private final IconOutput icon;

    public RecipeModifierOutput(RecipeModifier delegate) {
        this.delegate = delegate;
        this.icon = IconOutput.of(delegate.getIcon());
    }

    public int getId() {
        return delegate.getId();
    }

    public int getGameVersionId() {
        return delegate.getGameVersion().getId();
    }

    public String getDescription() {
        return delegate.getDescription();
    }

    public IconOutput getIcon() {
        return icon;
    }

    public Fraction getDurationMultiplier() {
        return delegate.getDurationMultiplier();
    }

    public Fraction getInputQuantityMultiplier() {
        return delegate.getInputQuantityMultiplier();
    }

    public Fraction getOutputQuantityMultiplier() {
        return delegate.getOutputQuantityMultiplier();
    }

}
