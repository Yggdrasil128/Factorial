package de.yggdrasil128.factorial.model.game;

import de.yggdrasil128.factorial.model.gameversion.GameVersionMigration;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class GameMigration {

    private String name;
    private Map<String, GameVersionMigration> versions = emptyMap();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, GameVersionMigration> getVersions() {
        return versions;
    }

    public void setVersions(Map<String, GameVersionMigration> versions) {
        this.versions = versions;
    }

}
