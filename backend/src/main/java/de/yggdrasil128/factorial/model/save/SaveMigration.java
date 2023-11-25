package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.changelist.ChangelistMigration;
import de.yggdrasil128.factorial.model.factory.FactoryMigration;
import de.yggdrasil128.factorial.model.transportline.TransportLineMigration;

import java.util.List;

import static java.util.Collections.emptyList;

public class SaveMigration {

    private String game;
    private String version;
    private String name;
    private List<FactoryMigration> factories;
    private List<ChangelistMigration> changelists;
    private List<TransportLineMigration> transportLines;

    public SaveMigration() {
        factories = emptyList();
        changelists = emptyList();
        transportLines = emptyList();
    }

    public SaveMigration(String game, String version, String name, List<FactoryMigration> factories,
                         List<ChangelistMigration> changelists, List<TransportLineMigration> transportLines) {
        this.game = game;
        this.version = version;
        this.name = name;
        this.factories = factories;
        this.changelists = changelists;
        this.transportLines = transportLines;
    }

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

    public List<ChangelistMigration> getChangelists() {
        return changelists;
    }

    public void setChangelists(List<ChangelistMigration> changelists) {
        this.changelists = changelists;
    }

    public List<TransportLineMigration> getTransportLines() {
        return transportLines;
    }

    public void setTransportLines(List<TransportLineMigration> transportLines) {
        this.transportLines = transportLines;
    }

}
