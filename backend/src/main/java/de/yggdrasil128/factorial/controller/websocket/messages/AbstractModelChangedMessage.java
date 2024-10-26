package de.yggdrasil128.factorial.controller.websocket.messages;

public class AbstractModelChangedMessage extends AbstractMessage {

    private final int saveId;

    public AbstractModelChangedMessage(String runtimeId, int messageId, int saveId) {
        super(runtimeId, messageId);
        this.saveId = saveId;
    }

    public int getSaveId() {
        return saveId;
    }
}
