package de.yggdrasil128.factorial.model.recipemodifier;

import de.yggdrasil128.factorial.model.game.GameRelatedEntityRemovedEvent;

public class RecipeModifierRemovedEvent extends GameRelatedEntityRemovedEvent {

    private final int recipeModifierId;

    public RecipeModifierRemovedEvent(int gameId, int recipeModifierId) {
        super(gameId);
        this.recipeModifierId = recipeModifierId;
    }

    public int getRecipeModifierId() {
        return recipeModifierId;
    }

}
