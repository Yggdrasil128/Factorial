package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.FractionConverter;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.save.Save;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ProductionStep {

    @Id
    @GeneratedValue
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
        modifiers = new ArrayList<>();
        applyBasics(standalone);
    }

    public void applyBasics(ProductionStepStandalone standalone) {
        OptionalInputField.of(standalone.machineCount()).apply(this::setMachineCount);
    }

    public static Object resolve(ProductionStep relation, External destination) {
        if (null == relation) {
            return null;
        }
        switch (destination) {
        case FRONTEND:
            return relation.getId();
        case SAVE_FILE:
            Factory factory = relation.getFactory();
            Save save = factory.getSave();
            return save.getFactories().indexOf(factory) + "." + factory.getProductionSteps().indexOf(relation);
        default:
            throw new AssertionError(
                    "unexpected enum constant: " + External.class.getCanonicalName() + '.' + destination.name());
        }
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
