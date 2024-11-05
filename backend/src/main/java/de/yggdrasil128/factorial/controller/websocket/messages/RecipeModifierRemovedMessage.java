package de.yggdrasil128.factorial.controller.websocket.messages;

public class RecipeModifierRemovedMessage extends GameRelatedModelChangedMessage {

    private final int recipeModifierId;

    public RecipeModifierRemovedMessage(String runtimeId, int messageId, int gameId, int recipeModifierId) {
        super(runtimeId, messageId, gameId);
        this.recipeModifierId = recipeModifierId;
    }

    public int getRecipeModifierId() {
        return recipeModifierId;
    }

}
