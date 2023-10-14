package de.yggdrasil128.factorial.api.items;

import de.yggdrasil128.factorial.model.recipe.Recipe;

public class ApiRecipe {

    private final Recipe delegate;
    private final ApiIcon icon;

    public ApiRecipe(Recipe delegate) {
        this.delegate = delegate;
        this.icon = null == delegate.getIcon() ? null : new ApiIcon(delegate.getIcon());
    }

    public int getId() {
        return delegate.getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public ApiIcon getIcon() {
        return icon;
    }

}
