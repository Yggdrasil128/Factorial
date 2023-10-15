package de.yggdrasil128.factorial.model.icon;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Icon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne
    @JsonBackReference
    private GameVersion gameVersion;
    @Column(nullable = false)
    private String name;
    @Lob
    @Column(nullable = false)
    private byte[] imageData;
    @Column(nullable = false)
    private String mimeType;
    @ElementCollection
    private List<String> category;

    public Icon() {
    }

    public Icon(GameVersion gameVersion, String name, byte[] imageData, String mimeType, List<String> category) {
        this.gameVersion = gameVersion;
        this.name = name;
        this.imageData = imageData;
        this.mimeType = mimeType;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GameVersion getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(GameVersion gameVersion) {
        this.gameVersion = gameVersion;
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
        return name + ": " + imageData.length + " bytes of " + mimeType;
    }
}
