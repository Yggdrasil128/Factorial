package de.yggdrasil128.factorial.model.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ItemStandalone {

    private int id;
    private int gameVersionId;
    private String name;
    private String description;
    private Object icon;
    private List<String> category;

    public ItemStandalone() {
    }

    public ItemStandalone(Item model) {
        this(model, RelationRepresentation.ID);
    }

    public ItemStandalone(Item model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        gameVersionId = model.getGameVersion().getId();
        name = model.getName();
        description = model.getDescription();
        icon = NamedModel.resolve(model.getIcon(), resolveStrategy);
        category = model.getCategory();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameVersionId() {
        return gameVersionId;
    }

    public void setGameVersionId(int gameVersionId) {
        this.gameVersionId = gameVersionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
