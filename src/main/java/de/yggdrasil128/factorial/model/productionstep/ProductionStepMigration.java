package de.yggdrasil128.factorial.model.productionstep;

import static java.util.Collections.emptyList;

import java.util.List;

import de.yggdrasil128.factorial.model.Fraction;

public class ProductionStepMigration {

    private String machineName;
    private String recipeName;
    private List<String> modifierNames = emptyList();
    private Fraction machineCount;

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public List<String> getModifierNames() {
        return modifierNames;
    }

    public void setModifierNames(List<String> modifierNames) {
        this.modifierNames = modifierNames;
    }

    public Fraction getMachineCount() {
        return machineCount;
    }

    public void setMachineCount(Fraction machineCount) {
        this.machineCount = machineCount;
    }

}
