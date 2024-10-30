package de.yggdrasil128.factorial.model.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record RecipeStandalone(@JsonProperty(access = READ_ONLY) int id,
                               @JsonProperty(access = READ_ONLY) int gameVersionId,
                               String name,
                               Object iconId,
                               List<ItemQuantityStandalone> ingredients,
                               List<ItemQuantityStandalone> products,
                               Fraction duration,
                               List<Object> applicableModifierIds,
                               List<Object> applicableMachineIds,
                               List<String> category) {

    @JsonCreator
    public static RecipeStandalone
            create(int id, int gameVersionId, String name, Object iconId, List<ItemQuantityStandalone> ingredients,
                   List<ItemQuantityStandalone> products, Fraction duration, List<Object> applicableModifierIds,
                   List<Object> applicableMachineIds, List<String> category) {
        return new RecipeStandalone(id, gameVersionId, name, iconId, ingredients, products, duration,
                applicableModifierIds, applicableMachineIds, category);
    }

    public RecipeStandalone(Recipe model) {
        this(model, RelationRepresentation.ID);
    }

    public RecipeStandalone(Recipe model, RelationRepresentation resolveStrategy) {
        this(model.getId(), model.getGameVersion().getId(), model.getName(),
                NamedModel.resolve(model.getIcon(), resolveStrategy),
                model.getIngredients().stream().map(resource -> new ItemQuantityStandalone(resource, resolveStrategy))
                        .toList(),
                model.getProducts().stream().map(resource -> new ItemQuantityStandalone(resource, resolveStrategy))
                        .toList(),
                model.getDuration(), NamedModel.resolve(model.getApplicableModifiers(), resolveStrategy),
                NamedModel.resolve(model.getApplicableMachines(), resolveStrategy), model.getCategory());
    }

}
