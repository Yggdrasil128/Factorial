package de.yggdrasil128.factorial.model.icon;

public class IconRemovedEvent {

    private final int gameId;
    private final int iconId;

    public IconRemovedEvent(int gameId, int iconId) {
        this.gameId = gameId;
        this.iconId = iconId;
    }

    public int getGameId() {
        return gameId;
    }

    public int getIconId() {
        return iconId;
    }

}
