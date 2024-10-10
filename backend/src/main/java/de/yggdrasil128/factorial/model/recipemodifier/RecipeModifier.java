package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.FractionConverter;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"game_version_id", "name"}))
public class RecipeModifier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    private GameVersion gameVersion;
    @Column(nullable = false)
    private String name;
    private String description;
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

    public RecipeModifier(GameVersion gameVersion, String name, String description, Icon icon,
                          Fraction durationMultiplier, Fraction inputQuantityMultiplier,
                          Fraction outputQuantityMultiplier) {
        this.gameVersion = gameVersion;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.durationMultiplier = durationMultiplier;
        this.inputQuantityMultiplier = inputQuantityMultiplier;
        this.outputQuantityMultiplier = outputQuantityMultiplier;
    }

    public int getId() {
        return id;
    }

    public GameVersion getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(GameVersion gameVersion) {
        this.gameVersion = gameVersion;
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
