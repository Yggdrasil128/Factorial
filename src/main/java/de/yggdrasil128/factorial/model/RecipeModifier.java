package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.FractionConverter;
import jakarta.persistence.*;

@Entity
public class RecipeModifier {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String description;
    @OneToOne
    private Icon icon;
    @Convert(converter = FractionConverter.class)
    private Fraction durationMultiplier = Fraction.ONE;
    @Convert(converter = FractionConverter.class)
    private Fraction inputQuantityMultiplier = Fraction.ONE;
    @Convert(converter = FractionConverter.class)
    private Fraction outputQuantityMultiplier = Fraction.ONE;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Fraction getDurationMultiplier() {
        return durationMultiplier;
    }

    public void setDurationMultiplier(Fraction durationMultiplier) {
        this.durationMultiplier = durationMultiplier;
    }

    public Fraction getInputQuantityMultiplier() {
        return inputQuantityMultiplier;
    }

    public void setInputQuantityMultiplier(Fraction inputQuantityMultiplier) {
        this.inputQuantityMultiplier = inputQuantityMultiplier;
    }

    public Fraction getOutputQuantityMultiplier() {
        return outputQuantityMultiplier;
    }

    public void setOutputQuantityMultiplier(Fraction outputQuantityMultiplier) {
        this.outputQuantityMultiplier = outputQuantityMultiplier;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        RecipeModifier recipeModifier = (RecipeModifier) that;

        return id == recipeModifier.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
