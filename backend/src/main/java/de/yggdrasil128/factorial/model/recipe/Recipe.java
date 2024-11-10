package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.FractionConverter;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"game_version_id", "name"}))
public class Recipe implements NamedModel {

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

    public Recipe(Game game, RecipeStandalone standalone) {
        this.game = game;
        ingredients = new ArrayList<>();
        products = new ArrayList<>();
        applicableModifiers = new ArrayList<>();
        applicableMachines = new ArrayList<>();
        applyBasics(standalone);
    }

    public void applyBasics(RecipeStandalone standalone) {
        OptionalInputField.of(standalone.name()).apply(this::setName);
        OptionalInputField.of(standalone.description()).apply(this::setDescription);
        OptionalInputField.of(standalone.duration()).apply(this::setDuration);
        OptionalInputField.of(standalone.category()).apply(this::setCategory);
    }

    @Override
    public int getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
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
