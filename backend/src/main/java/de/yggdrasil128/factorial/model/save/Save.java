package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.transportlink.TransportLink;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Save {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    private GameVersion gameVersion;
    @Column(nullable = false)
    private String name;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Factory> factories;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<TransportLink> transportLinks;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Changelist> changelists;

    public Save() {
    }

    public Save(GameVersion gameVersion, String name, List<Factory> factories, List<TransportLink> transportLinks,
                List<Changelist> changelists) {
        this.gameVersion = gameVersion;
        this.name = name;
        this.factories = factories;
        this.transportLinks = transportLinks;
        this.changelists = changelists;
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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Factory> getFactories() {
        return factories;
    }

    public void setFactories(List<Factory> factories) {
        this.factories = factories;
    }

    public List<TransportLink> getTransportLinks() {
        return transportLinks;
    }

    public void setTransportLinks(List<TransportLink> transportLinks) {
        this.transportLinks = transportLinks;
    }

    public List<Changelist> getChangelists() {
        return changelists;
    }

    public void setChangelists(List<Changelist> changelists) {
        this.changelists = changelists;
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
