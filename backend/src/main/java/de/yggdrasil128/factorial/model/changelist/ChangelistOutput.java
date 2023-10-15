package de.yggdrasil128.factorial.model.changelist;

import de.yggdrasil128.factorial.model.icon.IconOutput;

public class ChangelistOutput {

    private final Changelist delegate;
    private final IconOutput icon;

    public ChangelistOutput(Changelist delegate) {
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

    public boolean isPrimary() {
        return delegate.isPrimary();
    }

    public boolean isActive() {
        return delegate.isActive();
    }

    public IconOutput getIcon() {
        return icon;
    }

}
