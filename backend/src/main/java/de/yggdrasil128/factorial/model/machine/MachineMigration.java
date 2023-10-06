package de.yggdrasil128.factorial.model.machine;

import java.util.List;

import static java.util.Collections.emptyList;

public class MachineMigration {

    private List<String> machineModifierNames = emptyList();

    public List<String> getMachineModifierNames() {
        return machineModifierNames;
    }

    public void setMachineModifierNames(List<String> machineModifierNames) {
        this.machineModifierNames = machineModifierNames;
    }

}
