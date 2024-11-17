package de.yggdrasil128.factorial.controller.websocket.messages;

public class LocalResourceRemovedMessage extends SaveRelatedModelChangedMessage {

    private final int localResourceId;

    public LocalResourceRemovedMessage(String runtimeId, int messageId, int saveId, int localResourceId) {
        super(runtimeId, messageId, saveId);
        this.localResourceId = localResourceId;
    }

    public int getLocalResourceId() {
        return localResourceId;
    }
}
