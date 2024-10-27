package de.yggdrasil128.factorial.model.recipemodifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class RecipeModifierStandalone {

    @JsonProperty(access = READ_ONLY)
    private int id;
    @JsonProperty(access = READ_ONLY)
    private int gameVersionId;
    private String name;
    private String description;
    private Object iconId;
    private Fraction durationMultiplier;
    private Fraction inputQuantityMultiplier;
    private Fraction outputQuantityMultiplier;

    public RecipeModifierStandalone() {
    }

    public RecipeModifierStandalone(RecipeModifier model) {
        this(model, RelationRepresentation.ID);
    }

    public RecipeModifierStandalone(RecipeModifier model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        gameVersionId = model.getGameVersion().getId();
        name = model.getName();
        description = model.getDescription();
        iconId = NamedModel.resolve(model.getIcon(), resolveStrategy);
        durationMultiplier = model.getDurationMultiplier();
        inputQuantityMultiplier = model.getInputQuantityMultiplier();
        outputQuantityMultiplier = model.getOutputQuantityMultiplier();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getIconId() {
        return iconId;
    }

    public void setIconId(Object iconId) {
        this.iconId = iconId;
    }

    public Fraction getDurationMultiplier() {
        return durationMultiplier;
    }

    public void setDurationMultiplier(Fraction durationMultiplier) {
        this.durationMultiplier = durationMultiplier;
    }

    public Fraction getInputQuantityMultiplier() {
        return inputQuantityMultiplier;
    }

    public void setInputQuantityMultiplier(Fraction inputQuantityMultiplier) {
        this.inputQuantityMultiplier = inputQuantityMultiplier;
    }

    public Fraction getOutputQuantityMultiplier() {
        return outputQuantityMultiplier;
    }

    public void setOutputQuantityMultiplier(Fraction outputQuantityMultiplier) {
        this.outputQuantityMultiplier = outputQuantityMultiplier;
    }

}
