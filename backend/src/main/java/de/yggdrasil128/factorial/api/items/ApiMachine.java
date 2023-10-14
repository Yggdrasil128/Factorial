package de.yggdrasil128.factorial.api.items;

import de.yggdrasil128.factorial.model.machine.Machine;

public class ApiMachine {

    private final Machine delegate;
    private final ApiIcon icon;

    public ApiMachine(Machine delegate) {
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
