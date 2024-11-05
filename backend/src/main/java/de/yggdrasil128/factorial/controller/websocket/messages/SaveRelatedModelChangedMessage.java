package de.yggdrasil128.factorial.controller.websocket.messages;

public class SaveRelatedModelChangedMessage extends AbstractMessage {

    private final int saveId;

    public SaveRelatedModelChangedMessage(String runtimeId, int messageId, int saveId) {
        super(runtimeId, messageId);
        this.saveId = saveId;
    }

    public int getSaveId() {
        return saveId;
    }
}
