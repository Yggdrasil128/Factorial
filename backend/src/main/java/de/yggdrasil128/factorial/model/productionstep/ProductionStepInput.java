package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.Fraction;

import java.util.List;

public class ProductionStepInput {

    private int machineId;
    private int recipeId;
    private List<Integer> modifierIds;
    private Fraction machineCount;

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

}
