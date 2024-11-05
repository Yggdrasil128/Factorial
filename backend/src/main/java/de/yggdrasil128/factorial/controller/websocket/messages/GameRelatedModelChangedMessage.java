package de.yggdrasil128.factorial.controller.websocket.messages;

public class GameRelatedModelChangedMessage extends AbstractMessage {

    private final int gameId;

    public GameRelatedModelChangedMessage(String runtimeId, int messageId, int gameId) {
        super(runtimeId, messageId);
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

}
