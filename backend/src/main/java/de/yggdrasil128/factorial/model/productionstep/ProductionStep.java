package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.FractionConverter;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ProductionStep {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    private Factory factory;
    @ManyToOne(optional = false)
    private Machine machine;
    @ManyToOne(optional = false)
    private Recipe recipe;
    @ManyToMany
    private List<RecipeModifier> modifiers;
    @Convert(converter = FractionConverter.class)
    private Fraction machineCount = Fraction.ONE;

    public ProductionStep() {
    }

    public ProductionStep(Factory factory, ProductionStepStandalone standalone) {
        this.factory = factory;
        machine = null;
        recipe = null;
        modifiers = new ArrayList<>();
        machineCount = standalone.getMachineCount();
    }

    public ProductionStep(Factory factory, Machine machine, Recipe recipe, List<RecipeModifier> modifiers,
                          Fraction machineCount) {
        this.factory = factory;
        this.machine = machine;
        this.recipe = recipe;
        this.modifiers = modifiers;
        this.machineCount = machineCount;
    }

    public int getId() {
        return id;
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public List<RecipeModifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<RecipeModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public Fraction getMachineCount() {
        return machineCount;
    }

    public void setMachineCount(Fraction machineCount) {
        this.machineCount = machineCount;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        ProductionStep productionStep = (ProductionStep) that;

        return id == productionStep.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return recipe + " at " + machineCount + " " + machine;
    }
}
