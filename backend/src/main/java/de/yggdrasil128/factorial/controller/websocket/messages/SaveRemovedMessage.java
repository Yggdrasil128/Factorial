package de.yggdrasil128.factorial.controller.websocket.messages;

public class SaveRemovedMessage extends AbstractModelChangedMessage {

    public SaveRemovedMessage(String runtimeId, int messageId, int saveId) {
        super(runtimeId, messageId, saveId);
    }

}
