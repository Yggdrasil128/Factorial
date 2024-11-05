package de.yggdrasil128.factorial.model.game;

import java.util.Collection;

public class GamesReorderedEvent {

    private final Collection<Game> games;

    public GamesReorderedEvent(Collection<Game> games) {
        this.games = games;
    }

    public Collection<Game> getGames() {
        return games;
    }

}
