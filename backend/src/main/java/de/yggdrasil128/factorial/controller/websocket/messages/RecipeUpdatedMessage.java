package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;

public class RecipeUpdatedMessage extends GameRelatedModelChangedMessage {

    private final RecipeStandalone recipe;

    public RecipeUpdatedMessage(String runtimeId, int messageId, int gameId, RecipeStandalone recipe) {
        super(runtimeId, messageId, gameId);
        this.recipe = recipe;
    }

    public RecipeStandalone getRecipe() {
        return recipe;
    }

}
