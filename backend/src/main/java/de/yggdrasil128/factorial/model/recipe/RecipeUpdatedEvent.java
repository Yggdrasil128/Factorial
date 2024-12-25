package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.game.GameRelatedEvent;

public class RecipeUpdatedEvent implements GameRelatedEvent {

    private final Recipe recipe;

    public RecipeUpdatedEvent(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public int getGameId() {
        return recipe.getGame().getId();
    }

    public Recipe getRecipe() {
        return recipe;
    }

}
