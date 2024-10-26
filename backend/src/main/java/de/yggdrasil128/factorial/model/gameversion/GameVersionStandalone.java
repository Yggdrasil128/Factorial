package de.yggdrasil128.factorial.model.gameversion;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class GameVersionStandalone {

    @JsonProperty(access = READ_ONLY)
    private int id;
    private String name;
    private Object icon;

    public GameVersionStandalone() {
    }

    public GameVersionStandalone(GameVersion model) {
        this(model, RelationRepresentation.ID);
    }

    public GameVersionStandalone(GameVersion model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        name = model.getName();
        icon = NamedModel.resolve(model.getIcon(), resolveStrategy);
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

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

}
