package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.game.GameRelatedEntityRemovedEvent;

public class IconRemovedEvent extends GameRelatedEntityRemovedEvent {

    private final int iconId;

    public IconRemovedEvent(int gameId, int iconId) {
        super(gameId);
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

}
