package de.yggdrasil128.factorial.model.recipe;

import de.yggdrasil128.factorial.model.game.GameRelatedEntityRemovedEvent;

public class RecipeRemovedEvent extends GameRelatedEntityRemovedEvent {

    private final int recipeId;

    public RecipeRemovedEvent(int gameId, int recipeId) {
        super(gameId);
        this.recipeId = recipeId;
    }

    public int getRecipeId() {
        return recipeId;
    }

}
