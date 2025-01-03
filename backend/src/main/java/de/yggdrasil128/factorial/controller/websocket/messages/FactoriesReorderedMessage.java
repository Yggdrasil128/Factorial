package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.EntityPosition;

import java.util.List;

public class FactoriesReorderedMessage extends SaveRelatedModelChangedMessage {

    private final List<EntityPosition> order;

    public FactoriesReorderedMessage(String runtimeId, int messageId, int saveId, List<EntityPosition> order) {
        super(runtimeId, messageId, saveId);
        this.order = order;
    }

    public List<EntityPosition> getOrder() {
        return order;
    }

}
