package de.yggdrasil128.factorial.model.save;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class SaveStandalone {

    @JsonProperty(access = READ_ONLY)
    private int id;
    private Object gameVersionId;
    private String name;

    public SaveStandalone() {
    }

    public SaveStandalone(Save model) {
        this(model, RelationRepresentation.ID);
    }

    public SaveStandalone(Save model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        gameVersionId = NamedModel.resolve(model.getGameVersion(), resolveStrategy);
        name = model.getName();
    }

    public int getId() {
        return id;
    }

    public Object getGameVersionId() {
        return gameVersionId;
    }

    public void setGameVersionId(Object gameVersionId) {
        this.gameVersionId = gameVersionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
