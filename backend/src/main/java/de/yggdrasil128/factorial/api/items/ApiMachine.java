package de.yggdrasil128.factorial.api.items;

import de.yggdrasil128.factorial.model.machine.Machine;

public class ApiMachine {

    private final Machine delegate;

    public ApiMachine(Machine delegate) {
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
