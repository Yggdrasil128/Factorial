package de.yggdrasil128.factorial.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    private Icon icon;
    @ManyToMany
    private List<RecipeModifier> machineModifiers = List.of();

    public int getId() {
        return id;
    }

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
}
