package de.yggdrasil128.factorial.model.recipemodifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record RecipeModifierStandalone(@JsonProperty(access = READ_ONLY) int id,
                                       @JsonProperty(access = READ_ONLY) int gameId,
                                       String name,
                                       String description,
                                       Object iconId,
                                       Fraction durationMultiplier,
                                       Fraction inputQuantityMultiplier,
                                       Fraction outputQuantityMultiplier) {

    public static RecipeModifierStandalone of(RecipeModifier model) {
        return of(model, External.FRONTEND);
    }

    public static RecipeModifierStandalone of(RecipeModifier model, External destination) {
        return new RecipeModifierStandalone(model.getId(), model.getGame().getId(), model.getName(),
                model.getDescription(), NamedModel.resolve(model.getIcon(), destination), model.getDurationMultiplier(),
                model.getInputQuantityMultiplier(), model.getOutputQuantityMultiplier());
    }

}
