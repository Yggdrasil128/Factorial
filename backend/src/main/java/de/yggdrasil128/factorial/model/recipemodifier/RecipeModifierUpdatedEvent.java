package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.game.GameRelatedEvent;

public class RecipeModifierUpdatedEvent implements GameRelatedEvent {

    private final RecipeModifier recipeModifier;

    public RecipeModifierUpdatedEvent(RecipeModifier recipeModifier) {
        this.recipeModifier = recipeModifier;
    }

    @Override
    public int getGameId() {
        return recipeModifier.getGame().getId();
    }

    public RecipeModifier getRecipeModifier() {
        return recipeModifier;
    }

}
