package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.item.ItemStandalone;

public class ItemUpdatedMessage extends GameRelatedModelChangedMessage {

    private final ItemStandalone item;

    public ItemUpdatedMessage(String runtimeId, int messageId, int gameId, ItemStandalone item) {
        super(runtimeId, messageId, gameId);
        this.item = item;
    }

    public ItemStandalone getItem() {
        return item;
    }

}
