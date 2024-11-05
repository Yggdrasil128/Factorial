package de.yggdrasil128.factorial.controller.websocket.messages;

import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;

public class RecipeModifierUpdatedMessage extends GameRelatedModelChangedMessage {

    private RecipeModifierStandalone recipeModifier;

    public RecipeModifierUpdatedMessage(String runtimeId, int messageId, int gameId,
                                        RecipeModifierStandalone recipeModifier) {
        super(runtimeId, messageId, gameId);
        this.recipeModifier = recipeModifier;
    }

    public RecipeModifierStandalone getRecipeModifier() {
        return recipeModifier;
    }

}
