package de.yggdrasil128.factorial.model.icon;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class IconStandalone {

    private int id;
    private int gameVersionId;
    private String name;
    private byte[] imageData;
    private String mimeType;
    private List<String> category;

    public IconStandalone() {
    }

    public IconStandalone(Icon model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        gameVersionId = model.getGameVersion().getId();
        name = model.getName();
        imageData = model.getImageData();
        mimeType = model.getMimeType();
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
