package de.yggdrasil128.factorial.model.game;

import java.util.List;

import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Game {

    @Id
    @GeneratedValue
    private int id;
    @Column(nullable = false, unique = true)
    private String name;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<GameVersion> versions;

    public Game() {
    }

    public Game(String name, List<GameVersion> versions) {
        this.name = name;
        this.versions = versions;
    }

    public int getId() {
        return id;
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

    public List<GameVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<GameVersion> versions) {
        this.versions = versions;
    }

    @Override
    public String toString() {
        return name;
    }

}
