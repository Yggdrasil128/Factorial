package de.yggdrasil128.factorial.controller.websocket.messages;

public class ItemRemovedMessage extends GameRelatedModelChangedMessage {

    private final int itemId;

    public ItemRemovedMessage(String runtimeId, int messageId, int gameId, int itemId) {
        super(runtimeId, messageId, gameId);
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }

}
