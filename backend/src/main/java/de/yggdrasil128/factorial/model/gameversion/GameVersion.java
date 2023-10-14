package de.yggdrasil128.factorial.model.gameversion;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "name"}))
public class GameVersion {

    @Id
    @GeneratedValue
    private int id;
    @ManyToOne(optional = false)
    @JsonBackReference
    private Game game;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    private Icon icon;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Icon> icons;
    @JoinColumn
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Item> items;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Recipe> recipies;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<RecipeModifier> recipeModifiers;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Machine> machines;

    public GameVersion() {
    }

    public GameVersion(Game game, String name, Icon icon, List<Item> items, List<Recipe> recipies,
                       List<RecipeModifier> recipeModifiers, List<Machine> machines, List<Icon> icons) {
        this.game = game;
        this.name = name;
        this.icon = icon;
        this.items = items;
        this.recipies = recipies;
        this.recipeModifiers = recipeModifiers;
        this.machines = machines;
        this.icons = icons;
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

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public List<Icon> getIcons() {
        return icons;
    }

    public void setIcons(List<Icon> icons) {
        this.icons = icons;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Recipe> getRecipies() {
        return recipies;
    }

    public void setRecipies(List<Recipe> recipies) {
        this.recipies = recipies;
    }

    public List<RecipeModifier> getRecipeModifiers() {
        return recipeModifiers;
    }

    public void setRecipeModifiers(List<RecipeModifier> recipeModifiers) {
        this.recipeModifiers = recipeModifiers;
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }

    @Override
    public String toString() {
        return name;
    }

}
