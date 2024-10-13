package de.yggdrasil128.factorial.model.save;

import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

public class SaveStandalone {

    private int id;
    private Object gameVersion;
    private String name;

    public SaveStandalone() {
    }

    public SaveStandalone(Save model) {
        this(model, RelationRepresentation.ID);
    }

    public SaveStandalone(Save model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        gameVersion = NamedModel.resolve(model.getGameVersion(), resolveStrategy);
        name = model.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(Object gameVersion) {
        this.gameVersion = gameVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
