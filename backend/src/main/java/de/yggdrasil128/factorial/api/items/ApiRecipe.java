package de.yggdrasil128.factorial.api.items;

import de.yggdrasil128.factorial.model.recipe.Recipe;

public class ApiRecipe {

    private final Recipe delegate;

    public ApiRecipe(Recipe delegate) {
        this.delegate = delegate;
    }

    public int getId() {
        return delegate.getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public int getIconId() {
        return null == delegate.getIcon() ? 0 : delegate.getIcon().getId();
    }

}
