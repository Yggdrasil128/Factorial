package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.Fraction;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

public class ProductionStepMigration {

    private String machineName;
    private String recipeName;
    private List<String> modifierNames = emptyList();
    private Fraction machineCount;
    private Set<String> inputGreedNames = emptySet();
    private Set<String> outputGreedNames = emptySet();

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

    public Set<String> getInputGreedNames() {
        return inputGreedNames;
    }

    public void setInputGreedNames(Set<String> inputGreedNames) {
        this.inputGreedNames = inputGreedNames;
    }

    public Set<String> getOutputGreedNames() {
        return outputGreedNames;
    }

    public void setOutputGreedNames(Set<String> outputGreedNames) {
        this.outputGreedNames = outputGreedNames;
    }

}
