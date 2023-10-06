package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;

import java.util.List;

import static java.util.Collections.emptyList;

public class ProductionStepStandalone {

    private int machineId;
    private int recipeId;
    private List<Integer> modifierIds = emptyList();
    private Fraction machineCount = Fraction.ONE;

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

    ProductionStep with(Factory factory, Machine machine, Recipe recipe, List<RecipeModifier> modifiers) {
        return new ProductionStep(factory, machine, recipe, modifiers, machineCount);
    }

}
