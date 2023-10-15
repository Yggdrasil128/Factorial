package de.yggdrasil128.factorial.model.item;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"game_version_id", "name"}))
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    @JsonBackReference
    private GameVersion gameVersion;
    @Column(nullable = false)
    private String name;
    private String description;
    @ManyToOne
    private Icon icon;
    @ElementCollection
    private List<String> category;

    public Item() {
    }

    public Item(GameVersion gameVersion, String name, String description, Icon icon, List<String> category) {
        this.gameVersion = gameVersion;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.category = category;
    }

    public int getId() {
        return id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
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

        Item item = (Item) that;

        return id == item.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
