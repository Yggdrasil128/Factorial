package de.yggdrasil128.factorial.model.recipemodifier;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RecipeModifierStandalone {

    private int id;
    private int gameVersionId;
    private String name;
    private String description;
    private Object icon;
    private Fraction durationMultiplier;
    private Fraction inputQuantityMultiplier;
    private Fraction outputQuantityMultiplier;

    public RecipeModifierStandalone() {
    }

    public RecipeModifierStandalone(RecipeModifier model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        gameVersionId = model.getGameVersion().getId();
        name = model.getName();
        description = model.getDescription();
        icon = NamedModel.resolve(model.getIcon(), resolveStrategy);
        durationMultiplier = model.getDurationMultiplier();
        inputQuantityMultiplier = model.getInputQuantityMultiplier();
        outputQuantityMultiplier = model.getOutputQuantityMultiplier();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
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
