package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.factory.FactoryMigration;

import java.util.List;

import static java.util.Collections.emptyList;

public class SaveMigration {

    private String game;
    private String version;
    private String name;
    private List<FactoryMigration> factories = emptyList();

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FactoryMigration> getFactories() {
        return factories;
    }

    public void setFactories(List<FactoryMigration> factories) {
        this.factories = factories;
    }

}
