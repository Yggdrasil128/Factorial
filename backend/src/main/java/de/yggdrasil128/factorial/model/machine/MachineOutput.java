package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.icon.IconOutput;

import java.util.List;

public class MachineOutput {

    private final Machine delegate;
    private final IconOutput icon;

    public MachineOutput(Machine delegate) {
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

    public List<String> getCategory() {
        return delegate.getCategory();
    }

}
