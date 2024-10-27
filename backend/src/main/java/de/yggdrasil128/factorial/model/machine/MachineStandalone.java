package de.yggdrasil128.factorial.model.machine;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class MachineStandalone {

    @JsonProperty(access = READ_ONLY)
    private int id;
    @JsonProperty(access = READ_ONLY)
    private int gameVersionId;
    private String name;
    private Object iconId;
    private List<Object> machineModifierIds;
    private List<String> category;

    public MachineStandalone() {
    }

    public MachineStandalone(Machine model) {
        this(model, RelationRepresentation.ID);
    }

    public MachineStandalone(Machine model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        gameVersionId = model.getGameVersion().getId();
        name = model.getName();
        iconId = NamedModel.resolve(model.getIcon(), resolveStrategy);
        machineModifierIds = NamedModel.resolve(model.getMachineModifiers(), resolveStrategy);
        category = model.getCategory();
    }

    public int getId() {
        return id;
    }

    public int getGameVersionId() {
        return gameVersionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getIconId() {
        return iconId;
    }

    public void setIconId(Object iconId) {
        this.iconId = iconId;
    }

    public List<Object> getMachineModifierIds() {
        return machineModifierIds;
    }

    public void setMachineModifierIds(List<Object> machineModifierIds) {
        this.machineModifierIds = machineModifierIds;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
