package de.yggdrasil128.factorial.model.game;

public class GameOutput {

    private final Game delegate;

    public GameOutput(Game delegate) {
        this.delegate = delegate;
    }

    public int getId() {
        return delegate.getId();
    }

    public String getName() {
        return delegate.getName();
    }

}
