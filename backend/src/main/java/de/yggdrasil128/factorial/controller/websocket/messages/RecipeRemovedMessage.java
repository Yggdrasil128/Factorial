package de.yggdrasil128.factorial.controller.websocket.messages;

public class RecipeRemovedMessage extends GameRelatedModelChangedMessage {

    private final int recipeId;

    public RecipeRemovedMessage(String runtimeId, int messageId, int gameId, int recipeId) {
        super(runtimeId, messageId, gameId);
        this.recipeId = recipeId;
    }

    public int getRecipeId() {
        return recipeId;
    }

}
