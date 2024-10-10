package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.changelist.ChangelistMigration;
import de.yggdrasil128.factorial.model.factory.FactoryMigration;

import java.util.List;

import static java.util.Collections.emptyList;

public class SaveMigration {

    private String gameVersion;
    private String name;
    private List<FactoryMigration> factories;
    private List<ChangelistMigration> changelists;

    public SaveMigration() {
        factories = emptyList();
        changelists = emptyList();
    }

    public SaveMigration(String gameVersion, String name, List<FactoryMigration> factories,
                         List<ChangelistMigration> changelists) {
        this.gameVersion = gameVersion;
        this.name = name;
        this.factories = factories;
        this.changelists = changelists;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
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

    public List<ChangelistMigration> getChangelists() {
        return changelists;
    }

    public void setChangelists(List<ChangelistMigration> changelists) {
        this.changelists = changelists;
    }

}
