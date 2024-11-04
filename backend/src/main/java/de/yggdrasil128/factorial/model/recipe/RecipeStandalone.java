package de.yggdrasil128.factorial.model.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record RecipeStandalone(@JsonProperty(access = READ_ONLY) int id,
                               @JsonProperty(access = READ_ONLY) int gameId,
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
    create(int id, int gameId, String name, Object iconId, List<ItemQuantityStandalone> ingredients,
                   List<ItemQuantityStandalone> products, Fraction duration, List<Object> applicableModifierIds,
                   List<Object> applicableMachineIds, List<String> category) {
        return new RecipeStandalone(id, gameId, name, iconId, ingredients, products, duration,
                applicableModifierIds, applicableMachineIds, category);
    }

    public RecipeStandalone(Recipe model) {
        this(model, External.FRONTEND);
    }

    public RecipeStandalone(Recipe model, External destination) {
        this(model.getId(), model.getGame().getId(), model.getName(),
                NamedModel.resolve(model.getIcon(), destination),
                model.getIngredients().stream().map(resource -> new ItemQuantityStandalone(resource, destination))
                        .toList(),
                model.getProducts().stream().map(resource -> new ItemQuantityStandalone(resource, destination))
                        .toList(),
                model.getDuration(), NamedModel.resolve(model.getApplicableModifiers(), destination),
                NamedModel.resolve(model.getApplicableMachines(), destination), model.getCategory());
    }

}
