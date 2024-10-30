package de.yggdrasil128.factorial.model.recipemodifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record RecipeModifierStandalone(@JsonProperty(access = READ_ONLY) int id,
                                       @JsonProperty(access = READ_ONLY) int gameVersionId,
                                       String name,
                                       String description,
                                       Object iconId,
                                       Fraction durationMultiplier,
                                       Fraction inputQuantityMultiplier,
                                       Fraction outputQuantityMultiplier) {

    public static RecipeModifierStandalone of(RecipeModifier model) {
        return of(model, RelationRepresentation.ID);
    }

    public static RecipeModifierStandalone of(RecipeModifier model, RelationRepresentation resolveStrategy) {
        return new RecipeModifierStandalone(model.getId(), model.getGameVersion().getId(), model.getName(),
                model.getDescription(), NamedModel.resolve(model.getIcon(), resolveStrategy),
                model.getDurationMultiplier(), model.getInputQuantityMultiplier(), model.getOutputQuantityMultiplier());
    }

}
