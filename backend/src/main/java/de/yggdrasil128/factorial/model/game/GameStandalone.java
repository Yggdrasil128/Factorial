package de.yggdrasil128.factorial.model.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.NamedModel;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record GameStandalone(@JsonProperty(access = READ_ONLY) int id, String name, Object iconId) {

    public static GameStandalone of(Game model) {
        return of(model, External.FRONTEND);
    }

    public static GameStandalone of(Game model, External destination) {
        return new GameStandalone(model.getId(), model.getName(),
                NamedModel.resolve(model.getIcon(), destination));
    }

}
