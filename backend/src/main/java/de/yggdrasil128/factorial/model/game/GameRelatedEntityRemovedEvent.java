package de.yggdrasil128.factorial.model.game;

public abstract class GameRelatedEntityRemovedEvent implements GameRelatedEvent {

    private final int gameId;

    protected GameRelatedEntityRemovedEvent(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public int getGameId() {
        return gameId;
    }

}
