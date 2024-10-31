package de.yggdrasil128.factorial.model.machine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.External;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record MachineStandalone(@JsonProperty(access = READ_ONLY) int id,
                                @JsonProperty(access = READ_ONLY) int gameVersionId,
                                String name,
                                Object iconId,
                                List<Object> machineModifierIds,
                                List<String> category) {

    @JsonCreator
    public static MachineStandalone create(int id, int gameVersionId, String name, Object iconId,
                                           List<Object> machineModifierIds, List<String> category) {
        return new MachineStandalone(id, gameVersionId, name, iconId, machineModifierIds, category);
    }

    public MachineStandalone(Machine model) {
        this(model, External.FRONTEND);
    }

    public MachineStandalone(Machine model, External destination) {
        this(model.getId(), model.getGameVersion().getId(), model.getName(),
                NamedModel.resolve(model.getIcon(), destination),
                NamedModel.resolve(model.getMachineModifiers(), destination), model.getCategory());
    }

}
