package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.itemQuantity.ItemQuantityStandalone;

import java.util.List;

public class RecipeStandalone {

    private int id;
    private int gameVersionId;
    private String name;
    private Object icon;
    private List<ItemQuantityStandalone> ingredients;
    private List<ItemQuantityStandalone> products;
    private Fraction duration;
    private List<Object> applicableModifiers;
    private List<Object> applicableMachines;
    private List<String> category;

    public RecipeStandalone() {
    }

    public RecipeStandalone(Recipe model) {
        this(model, RelationRepresentation.ID);
    }

    public RecipeStandalone(Recipe model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        gameVersionId = model.getGameVersion().getId();
        name = model.getName();
        icon = NamedModel.resolve(model.getIcon(), resolveStrategy);
        ingredients = model.getIngredients().stream()
                .map(resource -> new ItemQuantityStandalone(resource, resolveStrategy)).toList();
        products = model.getProducts().stream().map(resource -> new ItemQuantityStandalone(resource, resolveStrategy))
                .toList();
        duration = model.getDuration();
        applicableModifiers = model.getApplicableModifiers().stream()
                .map(recipeModifier -> NamedModel.resolve(recipeModifier, resolveStrategy)).toList();
        applicableMachines = model.getApplicableMachines().stream()
                .map(machine -> NamedModel.resolve(machine, resolveStrategy)).toList();
        category = model.getCategory();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameVersionId() {
        return gameVersionId;
    }

    public void setGameVersionId(int gameVersionId) {
        this.gameVersionId = gameVersionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public List<ItemQuantityStandalone> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ItemQuantityStandalone> ingredients) {
        this.ingredients = ingredients;
    }

    public List<ItemQuantityStandalone> getProducts() {
        return products;
    }

    public void setProducts(List<ItemQuantityStandalone> products) {
        this.products = products;
    }

    public Fraction getDuration() {
        return duration;
    }

    public void setDuration(Fraction duration) {
        this.duration = duration;
    }

    public List<Object> getApplicableModifiers() {
        return applicableModifiers;
    }

    public void setApplicableModifiers(List<Object> applicableModifiers) {
        this.applicableModifiers = applicableModifiers;
    }

    public List<Object> getApplicableMachines() {
        return applicableMachines;
    }

    public void setApplicableMachines(List<Object> applicableMachines) {
        this.applicableMachines = applicableMachines;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
