package de.yggdrasil128.factorial.model.gameversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.External;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record GameVersionStandalone(@JsonProperty(access = READ_ONLY) int id, String name, Object iconId) {

    public static GameVersionStandalone of(GameVersion model) {
        return of(model, External.FRONTEND);
    }

    public static GameVersionStandalone of(GameVersion model, External destination) {
        return new GameVersionStandalone(model.getId(), model.getName(),
                NamedModel.resolve(model.getIcon(), destination));
    }

}
