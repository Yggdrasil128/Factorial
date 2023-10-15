package de.yggdrasil128.factorial.model.machine;

import java.util.List;

import static java.util.Collections.emptyList;

public class MachineMigration {

    private String iconName;
    private List<String> machineModifierNames = emptyList();
    private List<String> category = emptyList();

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

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
