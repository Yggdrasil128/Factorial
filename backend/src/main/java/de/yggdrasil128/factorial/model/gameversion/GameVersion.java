package de.yggdrasil128.factorial.model.gameversion;

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
    @Column(nullable = false)
    private String name;
    @ManyToOne
    private Icon icon;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Icon> icons;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Item> items;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Recipe> recipes;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<RecipeModifier> recipeModifiers;
    @JoinColumn
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Machine> machines;

    public GameVersion() {
    }

    public GameVersion(String name, Icon icon, List<Item> items, List<Recipe> recipes,
                       List<RecipeModifier> recipeModifiers, List<Machine> machines, List<Icon> icons) {
        this.name = name;
        this.icon = icon;
        this.items = items;
        this.recipes = recipes;
        this.recipeModifiers = recipeModifiers;
        this.machines = machines;
        this.icons = icons;
    }

    public int getId() {
        return id;
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

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
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
