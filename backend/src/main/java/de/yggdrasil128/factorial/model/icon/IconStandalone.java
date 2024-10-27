package de.yggdrasil128.factorial.model.icon;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class IconStandalone {

    @JsonProperty(access = READ_ONLY)
    private int id;
    @JsonProperty(access = READ_ONLY)
    private int gameVersionId;
    private String name;
    private byte[] imageData;
    private String mimeType;
    private List<String> category;

    public IconStandalone() {
    }

    public IconStandalone(Icon model) {
        this(model, RelationRepresentation.ID);
    }

    public IconStandalone(Icon model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        gameVersionId = model.getGameVersion().getId();
        name = model.getName();
        // TODO I guess we are starting to need something slightly more sophisticated
        if (RelationRepresentation.NAME == resolveStrategy) {
            imageData = model.getImageData();
        }
        mimeType = model.getMimeType();
        category = model.getCategory();
    }

    public int getId() {
        return id;
    }

    public int getGameVersionId() {
        return gameVersionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
