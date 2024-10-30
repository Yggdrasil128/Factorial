package de.yggdrasil128.factorial.model.machine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

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
        this(model, RelationRepresentation.ID);
    }

    public MachineStandalone(Machine model, RelationRepresentation resolveStrategy) {
        this(model.getId(), model.getGameVersion().getId(), model.getName(),
                NamedModel.resolve(model.getIcon(), resolveStrategy),
                NamedModel.resolve(model.getMachineModifiers(), resolveStrategy), model.getCategory());
    }

}
