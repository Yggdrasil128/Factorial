package de.yggdrasil128.factorial.model.gameversion;

import de.yggdrasil128.factorial.model.icon.IconOutput;

public class GameVersionOutput {

    private final GameVersion delegate;
    private final IconOutput icon;

    public GameVersionOutput(GameVersion delegate) {
        this.delegate = delegate;
        this.icon = IconOutput.of(delegate.getIcon());
    }

    public int getId() {
        return delegate.getId();
    }

    public int getGameId() {
        return delegate.getGame().getId();
    }

    public String getName() {
        return delegate.getName();
    }

    public IconOutput getIcon() {
        return icon;
    }

}
