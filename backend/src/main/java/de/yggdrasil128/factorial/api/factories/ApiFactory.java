package de.yggdrasil128.factorial.api.factories;

import de.yggdrasil128.factorial.api.items.ApiIcon;
import de.yggdrasil128.factorial.model.factory.Factory;

public class ApiFactory {

    private final Factory delegate;
    private final ApiIcon icon;

    public ApiFactory(Factory delegate) {
        this.delegate = delegate;
        this.icon = null == delegate.getIcon() ? null : new ApiIcon(delegate.getIcon());
    }

    public int getId() {
        return delegate.getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public int getOrdinal() {
        return delegate.getId();
    }

    public ApiIcon getIcon() {
        return icon;
    }

}
