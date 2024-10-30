package de.yggdrasil128.factorial.model.save;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record SaveStandalone(@JsonProperty(access = READ_ONLY) int id, Object gameVersionId, String name) {

    public static SaveStandalone of(Save model) {
        return of(model, RelationRepresentation.ID);
    }

    public static SaveStandalone of(Save model, RelationRepresentation resolveStrategy) {
        return new SaveStandalone(model.getId(), NamedModel.resolve(model.getGameVersion(), resolveStrategy),
                model.getName());
    }

}
