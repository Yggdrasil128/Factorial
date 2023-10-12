package de.yggdrasil128.factorial.model.icon;

import jakarta.persistence.*;

@Entity
public class Icon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private byte[] imageData;
    @Column(nullable = false)
    private String mimeType;

    public Icon() {
    }

    public Icon(int id, byte[] imageData, String mimeType) {
        this.id = id;
        this.imageData = imageData;
        this.mimeType = mimeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        Icon icon = (Icon) that;

        return id == icon.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return imageData.length + " bytes of " + mimeType;
    }
}
