package de.yggdrasil128.factorial.model.machine;

import java.util.List;

import static java.util.Collections.emptyList;

public class MachineMigration {

    private String iconName;
    private List<String> machineModifierNames = emptyList();

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public List<String> getMachineModifierNames() {
        return machineModifierNames;
    }

    public void setMachineModifierNames(List<String> machineModifierNames) {
        this.machineModifierNames = machineModifierNames;
    }

}
