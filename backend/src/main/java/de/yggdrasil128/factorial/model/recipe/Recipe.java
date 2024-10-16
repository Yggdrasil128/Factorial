package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.FractionConverter;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.itemQuantity.ItemQuantity;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"game_version_id", "name"}))
public class Recipe implements NamedModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(optional = false)
    private GameVersion gameVersion;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    private Icon icon;
    @ElementCollection
    private List<ItemQuantity> ingredients;
    @ElementCollection
    private List<ItemQuantity> products;
    @Column(nullable = false)
    @Convert(converter = FractionConverter.class)
    private Fraction duration;
    @ManyToMany
    private List<RecipeModifier> applicableModifiers;
    @ManyToMany
    private List<Machine> applicableMachines;
    @ElementCollection
    private List<String> category;

    public Recipe() {
    }

    public Recipe(GameVersion gameVersion, RecipeStandalone standalone) {
        this.gameVersion = gameVersion;
        name = standalone.getName();
        icon = null;
        ingredients = new ArrayList<>();
        products = new ArrayList<>();
        duration = standalone.getDuration();
        applicableModifiers = new ArrayList<>();
        applicableMachines = new ArrayList<>();
        category = standalone.getCategory();
    }

    public Recipe(GameVersion gameVersion, String name, Icon icon, List<ItemQuantity> input, List<ItemQuantity> output,
                  Fraction duration, List<RecipeModifier> applicableModifiers, List<Machine> applicableMachines,
                  List<String> category) {
        this.gameVersion = gameVersion;
        this.name = name;
        this.icon = icon;
        this.ingredients = input;
        this.products = output;
        this.duration = duration;
        this.applicableModifiers = applicableModifiers;
        this.applicableMachines = applicableMachines;
        this.category = category;
    }

    @Override
    public int getId() {
        return id;
    }

    public GameVersion getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(GameVersion gameVersion) {
        this.gameVersion = gameVersion;
    }

    @Override
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

    public List<ItemQuantity> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ItemQuantity> ingredients) {
        this.ingredients = ingredients;
    }

    public List<ItemQuantity> getProducts() {
        return products;
    }

    public void setProducts(List<ItemQuantity> products) {
        this.products = products;
    }

    public Fraction getDuration() {
        return duration;
    }

    public void setDuration(Fraction duration) {
        this.duration = duration;
    }

    public List<RecipeModifier> getApplicableModifiers() {
        return applicableModifiers;
    }

    public void setApplicableModifiers(List<RecipeModifier> applicableModifiers) {
        this.applicableModifiers = applicableModifiers;
    }

    public List<Machine> getApplicableMachines() {
        return applicableMachines;
    }

    public void setApplicableMachines(List<Machine> applicableMachines) {
        this.applicableMachines = applicableMachines;
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

        Recipe recipe = (Recipe) that;

        return id == recipe.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return ingredients + " to " + products + " in " + duration + " s";
    }
}
