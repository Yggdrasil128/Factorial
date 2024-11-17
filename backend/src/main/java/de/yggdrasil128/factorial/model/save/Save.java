package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.OrderedModel;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.resource.global.GlobalResource;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Save implements NamedModel, OrderedModel {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne(optional = false)
    private Game game;
    @Column(nullable = false)
    private int ordinal;
    // TODO validate uniqueness in service operations
    @Column(unique = true, nullable = false)
    private String name = "";
    private String description = "";
    @ManyToOne
    private Icon icon;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Factory> factories;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Changelist> changelists;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<GlobalResource> resources;

    public Save() {
    }

    public Save(Game game, SaveStandalone standalone) {
        this.game = game;
        factories = new ArrayList<>();
        changelists = new ArrayList<>();
        applyBasics(standalone);
    }

    public void applyBasics(SaveStandalone standalone) {
        OptionalInputField.of(standalone.name()).apply(this::setName);
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

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
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

    public List<Factory> getFactories() {
        return factories;
    }

    public void setFactories(List<Factory> factories) {
        this.factories = factories;
    }

    public List<Changelist> getChangelists() {
        return changelists;
    }

    public void setChangelists(List<Changelist> changelists) {
        this.changelists = changelists;
    }

    public List<GlobalResource> getResources() {
        return resources;
    }

    public void setResources(List<GlobalResource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Save && id == ((Save) obj).id;
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
