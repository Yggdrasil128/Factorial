package de.yggdrasil128.factorial.model.icon;

import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import jakarta.persistence.*;

import java.util.List;

import static java.util.stream.Collectors.joining;

@Entity
public class Icon implements NamedModel {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    private Game game;
    @Column(nullable = false)
    private String name = "";
    @Lob
    @Column(nullable = false)
    private byte[] imageData;
    @Column(nullable = false)
    private String mimeType = "";
    @ElementCollection
    private List<String> category;

    public Icon() {
    }

    public Icon(Game game, IconStandalone standalone) {
        this.game = game;
        applyBasics(standalone);
    }

    public void applyBasics(IconStandalone standalone) {
        OptionalInputField.of(standalone.name()).apply(this::setName);
        OptionalInputField.of(standalone.imageData()).apply(this::setImageData);
        OptionalInputField.of(standalone.mimeType()).apply(this::setMimeType);
        OptionalInputField.of(standalone.category()).apply(this::setCategory);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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
        return name + ": " + imageData.length + " bytes of " + mimeType
                + category.stream().collect(joining(".", " (", ")"));
    }

}
