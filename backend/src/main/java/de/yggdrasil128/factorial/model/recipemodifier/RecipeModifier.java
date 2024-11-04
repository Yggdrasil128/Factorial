package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.FractionConverter;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.icon.Icon;
import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"game_version_id", "name"}))
public class RecipeModifier implements NamedModel {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne(optional = false)
    private Game game;
    @Column(nullable = false)
    private String name = "";
    private String description = "";
    @OneToOne
    private Icon icon;
    @Column(nullable = false)
    @Convert(converter = FractionConverter.class)
    private Fraction durationMultiplier = Fraction.ONE;
    @Column(nullable = false)
    @Convert(converter = FractionConverter.class)
    private Fraction inputQuantityMultiplier = Fraction.ONE;
    @Column(nullable = false)
    @Convert(converter = FractionConverter.class)
    private Fraction outputQuantityMultiplier = Fraction.ONE;

    public RecipeModifier() {
    }

    public RecipeModifier(Game game, RecipeModifierStandalone standalone) {
        this.game = game;
        applyBasics(standalone);
    }

    public void applyBasics(RecipeModifierStandalone standalone) {
        OptionalInputField.of(standalone.name()).apply(this::setName);
        OptionalInputField.of(standalone.description()).apply(this::setDescription);
        OptionalInputField.of(standalone.durationMultiplier()).apply(this::setDurationMultiplier);
        OptionalInputField.of(standalone.inputQuantityMultiplier()).apply(this::setInputQuantityMultiplier);
        OptionalInputField.of(standalone.outputQuantityMultiplier()).apply(this::setOutputQuantityMultiplier);
    }

    @Override
    public int getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
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

    public Fraction getInputSpeedMultiplier() {
        return inputQuantityMultiplier.divide(durationMultiplier);
    }

    public Fraction getOutputSpeedMultiplier() {
        return outputQuantityMultiplier.divide(durationMultiplier);
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

    @Override
    public String toString() {
        return "duration x " + durationMultiplier + " | input x " + inputQuantityMultiplier + " | output x "
                + outputQuantityMultiplier;
    }

}
