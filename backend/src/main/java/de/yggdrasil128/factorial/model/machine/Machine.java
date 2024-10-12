package de.yggdrasil128.factorial.model.machine;

import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"game_version_id", "name"}))
public class Machine implements NamedModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    private GameVersion gameVersion;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    private Icon icon;
    @ManyToMany
    private List<RecipeModifier> machineModifiers;
    @ElementCollection
    private List<String> category;

    public Machine() {
    }

    public Machine(GameVersion gameVersion, MachineStandalone standalone) {
        this.gameVersion = gameVersion;
        name = standalone.getName();
        icon = null;
        machineModifiers = new ArrayList<>();
        category = standalone.getCategory();
    }

    public Machine(GameVersion gameVersion, String name, Icon icon, List<RecipeModifier> machineModifiers,
                   List<String> category) {
        this.gameVersion = gameVersion;
        this.name = name;
        this.icon = icon;
        this.machineModifiers = machineModifiers;
        this.category = category;
    }

    @Override
    public int getId() {
        return id;
    }

    public GameVersion getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(GameVersion gameVersion) {
        this.gameVersion = gameVersion;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
