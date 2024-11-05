package de.yggdrasil128.factorial.controller.websocket.messages;

public class IconRemovedMessage extends GameRelatedModelChangedMessage {

    private final int iconId;

    public IconRemovedMessage(String runtimeId, int messageId, int gameId, int iconId) {
        super(runtimeId, messageId, gameId);
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

}
