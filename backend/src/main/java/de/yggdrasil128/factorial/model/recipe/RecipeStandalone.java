package de.yggdrasil128.factorial.model.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.itemQuantity.ItemQuantityStandalone;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class RecipeStandalone {

    @JsonProperty(access = READ_ONLY)
    private int id;
    @JsonProperty(access = READ_ONLY)
    private int gameVersionId;
    private String name;
    private Object iconId;
    private List<ItemQuantityStandalone> ingredients;
    private List<ItemQuantityStandalone> products;
    private Fraction duration;
    private List<Object> applicableModifierIds;
    private List<Object> applicableMachineIds;
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
        iconId = NamedModel.resolve(model.getIcon(), resolveStrategy);
        ingredients = model.getIngredients().stream()
                .map(resource -> new ItemQuantityStandalone(resource, resolveStrategy)).toList();
        products = model.getProducts().stream().map(resource -> new ItemQuantityStandalone(resource, resolveStrategy))
                .toList();
        duration = model.getDuration();
        applicableModifierIds = NamedModel.resolve(model.getApplicableModifiers(), resolveStrategy);
        applicableMachineIds = NamedModel.resolve(model.getApplicableMachines(), resolveStrategy);
        category = model.getCategory();
    }

    public int getId() {
        return id;
    }

    public int getGameVersionId() {
        return gameVersionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getIconId() {
        return iconId;
    }

    public void setIconId(Object iconId) {
        this.iconId = iconId;
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

    public List<Object> getApplicableModifierIds() {
        return applicableModifierIds;
    }

    public void setApplicableModifierIds(List<Object> applicableModifierIds) {
        this.applicableModifierIds = applicableModifierIds;
    }

    public List<Object> getApplicableMachineIds() {
        return applicableMachineIds;
    }

    public void setApplicableMachineIds(List<Object> applicableMachineIds) {
        this.applicableMachineIds = applicableMachineIds;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

}
