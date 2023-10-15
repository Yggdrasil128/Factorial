package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.icon.IconOutput;

import java.util.List;

public class RecipeOutput {

    private final Recipe delegate;
    private final IconOutput icon;

    public RecipeOutput(Recipe delegate) {
        this.delegate = delegate;
        this.icon = IconOutput.of(delegate.getIcon());
    }

    public int getId() {
        return delegate.getId();
    }

    public int getGameVersionId() {
        return delegate.getGameVersion().getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public IconOutput getIcon() {
        return icon;
    }

    public Fraction getDuration() {
        return delegate.getDuration();
    }

    public List<String> getCategory() {
        return delegate.getCategory();
    }

}
