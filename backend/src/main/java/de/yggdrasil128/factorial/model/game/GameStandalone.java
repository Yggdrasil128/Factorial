package de.yggdrasil128.factorial.model.game;

import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.NamedModel;

public record GameStandalone(int id,
                             Integer ordinal,
                             String name,
                             String description,
                             Object iconId) {

    public static GameStandalone of(Game model) {
        return of(model, External.FRONTEND);
    }

    public static GameStandalone of(Game model, External destination) {
        return new GameStandalone(model.getId(), model.getOrdinal(), model.getName(), model.getDescription(),
                NamedModel.resolve(model.getIcon(), destination));
    }

}
