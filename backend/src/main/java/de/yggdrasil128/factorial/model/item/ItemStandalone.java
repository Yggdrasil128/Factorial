package de.yggdrasil128.factorial.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.NamedModel;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record ItemStandalone(int id,
                             @JsonProperty(access = READ_ONLY) int gameId,
                             String name,
                             String description,
                             Object iconId,
                             List<String> category) {

    public static ItemStandalone of(Item model) {
        return of(model, External.FRONTEND);
    }

    public static ItemStandalone of(Item model, External destination) {
        return new ItemStandalone(model.getId(), model.getGame().getId(), model.getName(), model.getDescription(),
                NamedModel.resolve(model.getIcon(), destination), new ArrayList<>(model.getCategory()));
    }

}
