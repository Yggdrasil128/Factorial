package de.yggdrasil128.factorial.model.machine;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.NamedModel;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record MachineStandalone(int id,
                                @JsonProperty(access = READ_ONLY) int gameId,
                                String name,
                                String description,
                                Object iconId,
                                List<Object> machineModifierIds,
                                List<String> category) {

    public static MachineStandalone of(Machine model) {
        return of(model, External.FRONTEND);
    }

    public static MachineStandalone of(Machine model, External destination) {
        return new MachineStandalone(model.getId(), model.getGame().getId(), model.getName(), model.getDescription(),
                NamedModel.resolve(model.getIcon(), destination),
                NamedModel.resolve(model.getMachineModifiers(), destination), new ArrayList<>(model.getCategory()));
    }

}
