package de.yggdrasil128.factorial.controller.websocket.messages;

public class SaveRemovedMessage extends SaveRelatedModelChangedMessage {

    public SaveRemovedMessage(String runtimeId, int messageId, int saveId) {
        super(runtimeId, messageId, saveId);
    }

}
