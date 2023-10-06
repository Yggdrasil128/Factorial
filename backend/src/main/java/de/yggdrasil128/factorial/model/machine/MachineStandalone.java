package de.yggdrasil128.factorial.model.machine;

import java.util.List;

import static java.util.Collections.emptyList;

public class MachineStandalone {

    private String name;
    private int iconId;
    private List<Integer> machineModifierIds = emptyList();

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

}
