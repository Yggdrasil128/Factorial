package de.yggdrasil128.factorial.controller.websocket.messages;

public class GlobalResourceRemovedMessage extends SaveRelatedModelChangedMessage {

    private final int glboalResourceId;

    public GlobalResourceRemovedMessage(String runtimeId, int messageId, int saveId, int globalResourceId) {
        super(runtimeId, messageId, saveId);
        this.glboalResourceId = globalResourceId;
    }

    public int getGlobalResourceId() {
        return glboalResourceId;
    }
}
