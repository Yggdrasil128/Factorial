package de.yggdrasil128.factorial.model.icon;

import java.util.List;

import static java.util.Collections.emptyList;

public class IconMigration {

    private byte[] imageData;
    private String mimeType;
    private List<String> category = emptyList();

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
