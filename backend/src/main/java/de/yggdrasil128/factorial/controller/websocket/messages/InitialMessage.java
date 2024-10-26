package de.yggdrasil128.factorial.controller.websocket.messages;

public class InitialMessage extends AbstractMessage {

    public InitialMessage(String runtimeId, int messageId) {
        super(runtimeId, messageId);
    }
}
