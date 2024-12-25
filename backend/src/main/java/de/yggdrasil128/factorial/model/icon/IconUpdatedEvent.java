package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.game.GameRelatedEvent;

public class IconUpdatedEvent implements GameRelatedEvent {

    private final Icon icon;

    public IconUpdatedEvent(Icon icon) {
        this.icon = icon;
    }

    @Override
    public int getGameId() {
        return icon.getGame().getId();
    }

    public Icon getIcon() {
        return icon;
    }

}
