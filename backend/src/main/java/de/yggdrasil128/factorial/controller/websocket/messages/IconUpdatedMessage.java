package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.icon.IconStandalone;

public class IconUpdatedMessage extends GameRelatedModelChangedMessage {

    private final IconStandalone icon;

    public IconUpdatedMessage(String runtimeId, int messageId, int gameId, IconStandalone icon) {
        super(runtimeId, messageId, gameId);
        this.icon = icon;
    }

    public IconStandalone getIcon() {
        return icon;
    }

}
