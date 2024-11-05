package de.yggdrasil128.factorial.model.recipemodifier;

public class RecipeModifierRemovedEvent {

    private final int gameId;
    private final int recipeModifierId;

    public RecipeModifierRemovedEvent(int gameId, int recipeModifierId) {
        this.gameId = gameId;
        this.recipeModifierId = recipeModifierId;
    }

    public int getGameId() {
        return gameId;
    }

    public int getRecipeModifierId() {
        return recipeModifierId;
    }

}
