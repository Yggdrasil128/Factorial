package de.yggdrasil128.factorial.model.machine;

import static java.util.Collections.emptyList;

import java.util.List;

public class MachineMigration {

    private List<String> machineModifierNames = emptyList();

    public List<String> getMachineModifierNames() {
        return machineModifierNames;
    }

    public void setMachineModifierNames(List<String> machineModifierNames) {
        this.machineModifierNames = machineModifierNames;
    }

}
