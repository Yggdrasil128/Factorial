package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.game.GameStandalone;

public class GameUpdatedMessage extends GameRelatedModelChangedMessage {

    private final GameStandalone game;

    public GameUpdatedMessage(String runtimeId, int messageId, int gameId, GameStandalone game) {
        super(runtimeId, messageId, gameId);
        this.game = game;
    }

    public GameStandalone getGame() {
        return game;
    }

}
