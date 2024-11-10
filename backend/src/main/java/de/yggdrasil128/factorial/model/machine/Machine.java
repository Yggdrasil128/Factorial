package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"game_version_id", "name"}))
public class Machine implements NamedModel {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne(optional = false)
    private Game game;
    @Column(nullable = false)
    private String name = "";
    private String description = "";
    @ManyToOne
    private Icon icon;
    @ManyToMany
    private List<RecipeModifier> machineModifiers;
    @ElementCollection
    private List<String> category;

    public Machine() {
    }

    public Machine(Game game, MachineStandalone standalone) {
        this.game = game;
        machineModifiers = new ArrayList<>();
        applyBasics(standalone);
    }

    public void applyBasics(MachineStandalone standalone) {
        OptionalInputField.of(standalone.name()).apply(this::setName);
        OptionalInputField.of(standalone.description()).apply(this::setDescription);
        OptionalInputField.of(standalone.category()).apply(this::setCategory);
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

    public List<RecipeModifier> getMachineModifiers() {
        return machineModifiers;
    }

    public void setMachineModifiers(List<RecipeModifier> machineModifiers) {
        this.machineModifiers = machineModifiers;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        Machine machine = (Machine) that;

        return id == machine.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
