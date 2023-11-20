package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.Fraction;

import java.util.List;

import static java.util.Collections.emptyList;

public class ProductionStepMigration {

    private String machineName;
    private String recipeName;
    private List<String> modifierNames = emptyList();
    private Fraction machineCount;
    private List<String> uncloggingInputNames = emptyList();
    private List<String> uncloggingOutputNames = emptyList();

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

    public List<String> getUncloggingInputNames() {
        return uncloggingInputNames;
    }

    public void setUncloggingInputNames(List<String> uncloggingInputNames) {
        this.uncloggingInputNames = uncloggingInputNames;
    }

    public List<String> getUncloggingOutputNames() {
        return uncloggingOutputNames;
    }

    public void setUncloggingOutputNames(List<String> uncloggingOutputNames) {
        this.uncloggingOutputNames = uncloggingOutputNames;
    }

}
