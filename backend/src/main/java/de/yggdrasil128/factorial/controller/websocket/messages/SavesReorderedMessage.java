package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.EntityPosition;

import java.util.List;

public class SavesReorderedMessage extends AbstractMessage {

    private final List<EntityPosition> order;

    public SavesReorderedMessage(String runtimeId, int messageId, List<EntityPosition> order) {
        super(runtimeId, messageId);
        this.order = order;
    }

    public List<EntityPosition> getOrder() {
        return order;
    }

}
