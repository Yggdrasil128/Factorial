package de.yggdrasil128.factorial.model.item;

import de.yggdrasil128.factorial.model.icon.IconOutput;

import java.util.List;

public class ItemOutput {

    private final Item delegate;
    private final IconOutput icon;

    public ItemOutput(Item delegate) {
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

    public String getDescription() {
        return delegate.getDescription();
    }

    public IconOutput getIcon() {
        return icon;
    }

    public List<String> getCategory() {
        return delegate.getCategory();
    }

}
