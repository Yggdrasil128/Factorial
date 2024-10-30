package de.yggdrasil128.factorial.model.icon;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record IconStandalone(@JsonProperty(access = READ_ONLY) int id,
                             @JsonProperty(access = READ_ONLY) int gameVersionId,
                             String name,
                             byte[] imageData,
                             String mimeType,
                             List<String> category) {

    public static IconStandalone of(Icon model) {
        return of(model, RelationRepresentation.ID);
    }

    public static IconStandalone of(Icon model, RelationRepresentation resolveStrategy) {
        return new IconStandalone(model.getId(), model.getGameVersion().getId(), model.getName(),
                RelationRepresentation.NAME == resolveStrategy ? model.getImageData() : null, model.getMimeType(),
                model.getCategory());
    }

}
