package de.yggdrasil128.factorial.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record ItemStandalone(@JsonProperty(access = READ_ONLY) int id,
                             @JsonProperty(access = READ_ONLY) int gameVersionId,
                             String name,
                             String description,
                             Object iconId,
                             List<String> category) {

    public static ItemStandalone of(Item model) {
        return of(model, RelationRepresentation.ID);
    }

    public static ItemStandalone of(Item model, RelationRepresentation resolveStrategy) {
        return new ItemStandalone(model.getId(), model.getGameVersion().getId(), model.getName(),
                model.getDescription(), NamedModel.resolve(model.getIcon(), resolveStrategy), model.getCategory());
    }

}
