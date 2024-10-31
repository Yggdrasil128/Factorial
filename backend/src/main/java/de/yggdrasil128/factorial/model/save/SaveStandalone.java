package de.yggdrasil128.factorial.model.save;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.External;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record SaveStandalone(@JsonProperty(access = READ_ONLY) int id, Object gameVersionId, String name) {

    public static SaveStandalone of(Save model) {
        return of(model, External.FRONTEND);
    }

    public static SaveStandalone of(Save model, External destination) {
        return new SaveStandalone(model.getId(), NamedModel.resolve(model.getGameVersion(), destination),
                model.getName());
    }

}
