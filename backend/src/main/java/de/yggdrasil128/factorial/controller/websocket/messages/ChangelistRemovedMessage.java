package de.yggdrasil128.factorial.controller.websocket.messages;

public class ChangelistRemovedMessage extends AbstractModelChangedMessage {

    private final int changelistId;

    public ChangelistRemovedMessage(String runtimeId, int messageId, int saveId, int changelistId) {
        super(runtimeId, messageId, saveId);
        this.changelistId = changelistId;
    }

    public int getChangelistId() {
        return changelistId;
    }
}
