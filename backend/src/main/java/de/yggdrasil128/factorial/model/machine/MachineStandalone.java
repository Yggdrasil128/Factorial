package de.yggdrasil128.factorial.model.machine;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class MachineStandalone {

    private int id;
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
        machineModifiers = model.getMachineModifiers().stream()
                .map(machineModifier -> NamedModel.resolve(machineModifier, resolveStrategy)).toList();
        category = model.getCategory();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameVersionId() {
        return gameVersionId;
    }

    public void setGameVersionId(int gameVersionId) {
        this.gameVersionId = gameVersionId;
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
