package de.yggdrasil128.factorial.model.machine;

import java.util.List;

public class MachineInput {

    private String name;
    private int iconId;
    private List<Integer> machineModifierIds;
    private List<String> category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public List<Integer> getMachineModifierIds() {
        return machineModifierIds;
    }

    public void setMachineModifierIds(List<Integer> machineModifierIds) {
        this.machineModifierIds = machineModifierIds;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
