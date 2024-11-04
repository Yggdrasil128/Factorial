package de.yggdrasil128.factorial.model.item;

import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.icon.Icon;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"game_version_id", "name"}))
public class Item implements NamedModel {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne(optional = false)
    private Game game;
    @Column(nullable = false)
    private String name = "";
    private String description = "";
    @ManyToOne
    private Icon icon;
    @ElementCollection
    private List<String> category;

    public Item() {
    }

    public Item(Game game, ItemStandalone standalone) {
        this.game = game;
        applyBasics(standalone);
    }

    public void applyBasics(ItemStandalone standalone) {
        OptionalInputField.of(standalone.name()).apply(this::setName);
        OptionalInputField.of(standalone.description()).apply(this::setDescription);
        OptionalInputField.of(standalone.category()).apply(this::setCategory);
    }

    public int getId() {
        return id;
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
