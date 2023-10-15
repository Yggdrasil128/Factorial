package de.yggdrasil128.factorial.model.save;

public class SaveOutput {

    private final Save delegate;

    public SaveOutput(Save delegate) {
        this.delegate = delegate;
    }

    public int getId() {
        return delegate.getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public int getGameVersionId() {
        return delegate.getGameVersion().getId();
    }

}
