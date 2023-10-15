package de.yggdrasil128.factorial.model.factory;

import de.yggdrasil128.factorial.model.icon.IconOutput;

public class FactoryOutput {

    private final Factory delegate;
    private final IconOutput icon;

    public FactoryOutput(Factory delegate) {
        this.delegate = delegate;
        this.icon = IconOutput.of(delegate.getIcon());
    }

    public int getId() {
        return delegate.getId();
    }

    public int getSaveId() {
        return delegate.getSave().getId();
    }

    public int getOrdinal() {
        return delegate.getOrdinal();
    }

    public String getName() {
        return delegate.getName();
    }

    public IconOutput getIcon() {
        return icon;
    }

}
