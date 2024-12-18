package de.yggdrasil128.factorial.model.icon;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.External;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record IconStandalone(int id,
                             @JsonProperty(access = READ_ONLY) int gameId,
                             String name,
                             byte[] imageData,
                             String imageUrl,
                             String mimeType,
                             @JsonProperty(access = READ_ONLY) long lastUpdated,
                             List<String> category) {

    public static IconStandalone of(Icon model) {
        return of(model, External.FRONTEND);
    }

    public static IconStandalone of(Icon model, External destination) {
        return new IconStandalone(model.getId(), model.getGame().getId(), model.getName(),
                External.SAVE_FILE == destination ? model.getImageData() : null, null, model.getMimeType(),
                model.getLastUpdated().toEpochMilli(), new ArrayList<>(model.getCategory()));
    }

}
