package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.FractionConverter;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    @ElementCollection
    private List<ItemWithQuantity> input = List.of();
    @ElementCollection
    private List<ItemWithQuantity> output = List.of();
    @Convert(converter = FractionConverter.class)
    private Fraction duration;
    @ManyToMany
    private List<RecipeModifier> applicableModifiers = List.of();
    @ManyToMany
    private List<Machine> applicableMachines = List.of();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemWithQuantity> getInput() {
        return input;
    }

    public void setInput(List<ItemWithQuantity> input) {
        this.input = input;
    }

    public List<ItemWithQuantity> getOutput() {
        return output;
    }

    public void setOutput(List<ItemWithQuantity> output) {
        this.output = output;
    }

    public Fraction getDuration() {
        return duration;
    }

    public void setDuration(Fraction duration) {
        this.duration = duration;
    }

    public List<RecipeModifier> getApplicableModifiers() {
        return applicableModifiers;
    }

    public void setApplicableModifiers(List<RecipeModifier> applicableModifiers) {
        this.applicableModifiers = applicableModifiers;
    }

    public List<Machine> getApplicableMachines() {
        return applicableMachines;
    }

    public void setApplicableMachines(List<Machine> applicableMachines) {
        this.applicableMachines = applicableMachines;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        Recipe recipe = (Recipe) that;

        return id == recipe.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
