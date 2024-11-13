package de.yggdrasil128.factorial.model.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record RecipeStandalone(int id,
                               @JsonProperty(access = READ_ONLY) int gameId,
                               String name,
                               String description,
                               Object iconId,
                               List<ItemQuantityStandalone> ingredients,
                               List<ItemQuantityStandalone> products,
                               Fraction duration,
                               List<Object> applicableModifierIds,
                               List<Object> applicableMachineIds,
                               List<String> category) {

    public static RecipeStandalone of(Recipe model) {
        return of(model, External.FRONTEND);
    }

    public static RecipeStandalone of(Recipe model, External destination) {
        return new RecipeStandalone(model.getId(), model.getGame().getId(), model.getName(), model.getDescription(),
                NamedModel.resolve(model.getIcon(), destination),
                model.getIngredients().stream().map(resource -> ItemQuantityStandalone.of(resource, destination))
                        .toList(),
                model.getProducts().stream().map(resource -> ItemQuantityStandalone.of(resource, destination)).toList(),
                model.getDuration(), NamedModel.resolve(model.getApplicableModifiers(), destination),
                NamedModel.resolve(model.getApplicableMachines(), destination), new ArrayList<>(model.getCategory()));
    }

}
