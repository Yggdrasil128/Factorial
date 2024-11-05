package de.yggdrasil128.factorial.model.game;

public class GameRemovedEvent {

    private final int gameId;

    public GameRemovedEvent(int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

}
