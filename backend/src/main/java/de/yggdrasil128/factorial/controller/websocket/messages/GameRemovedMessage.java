package de.yggdrasil128.factorial.controller.websocket.messages;

public class GameRemovedMessage extends GameRelatedModelChangedMessage {

    public GameRemovedMessage(String runtimeId, int messageId, int gameId) {
        super(runtimeId, messageId, gameId);
    }

}
