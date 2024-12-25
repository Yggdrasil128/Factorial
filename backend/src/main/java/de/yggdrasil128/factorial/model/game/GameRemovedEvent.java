package de.yggdrasil128.factorial.model.game;

public class GameRemovedEvent extends GameRelatedEntityRemovedEvent {

    public GameRemovedEvent(int gameId) {
        super(gameId);
    }

}
