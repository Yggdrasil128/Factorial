package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.Fraction;

import java.util.List;
import java.util.Set;

public class ProductionStepInput {

    private int machineId;
    private int recipeId;
    private List<Integer> modifierIds;
    private Fraction machineCount;
    private Set<Integer> inputGreedIds;
    private Set<Integer> outputGreedIds;

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public List<Integer> getModifierIds() {
        return modifierIds;
    }

    public void setModifierIds(List<Integer> modifierIds) {
        this.modifierIds = modifierIds;
    }

    public Fraction getMachineCount() {
        return machineCount;
    }

    public void setMachineCount(Fraction machineCount) {
        this.machineCount = machineCount;
    }

    public Set<Integer> getInputGreedIds() {
        return inputGreedIds;
    }

    public void setInputGreedIds(Set<Integer> inputGreedIds) {
        this.inputGreedIds = inputGreedIds;
    }

    public Set<Integer> getOutputGreedIds() {
        return outputGreedIds;
    }

    public void setOutputGreedIds(Set<Integer> outputGreedIds) {
        this.outputGreedIds = outputGreedIds;
    }

}
