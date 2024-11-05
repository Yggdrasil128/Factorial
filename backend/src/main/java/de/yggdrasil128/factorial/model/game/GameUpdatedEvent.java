package de.yggdrasil128.factorial.model.game;

public class GameUpdatedEvent {

    private final Game game;

    public GameUpdatedEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

}
