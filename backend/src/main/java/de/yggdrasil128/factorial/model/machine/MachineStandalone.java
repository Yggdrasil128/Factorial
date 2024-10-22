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
    private Object icon;
    private List<Object> machineModifiers;
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
        icon = NamedModel.resolve(model.getIcon(), resolveStrategy);
        machineModifiers = NamedModel.resolve(model.getMachineModifiers(), resolveStrategy);
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

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public List<Object> getMachineModifiers() {
        return machineModifiers;
    }

    public void setMachineModifiers(List<Object> machineModifiers) {
        this.machineModifiers = machineModifiers;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
