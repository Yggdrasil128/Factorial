package de.yggdrasil128.factorial.controller.websocket.messages;

public class ResourceRemovedMessage extends SaveRelatedModelChangedMessage {

    private final int resourceId;

    public ResourceRemovedMessage(String runtimeId, int messageId, int saveId, int resourceId) {
        super(runtimeId, messageId, saveId);
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }
}
