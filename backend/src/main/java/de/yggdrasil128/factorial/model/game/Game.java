package de.yggdrasil128.factorial.model.game;

import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue
    private int id;
    @Column(nullable = false, unique = true)
    private String name;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<GameVersion> gameVersions;

    public Game() {
    }

    public Game(String name, List<GameVersion> gameVersions) {
        this.name = name;
        this.gameVersions = gameVersions;
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

    public List<GameVersion> getGameVersions() {
        return gameVersions;
    }

    public void setVersions(List<GameVersion> gameVersions) {
        this.gameVersions = gameVersions;
    }

    @Override
    public String toString() {
        return name;
    }

}
