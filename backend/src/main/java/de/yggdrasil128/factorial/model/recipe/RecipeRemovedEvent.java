package de.yggdrasil128.factorial.model.recipe;

public class RecipeRemovedEvent {

    private final int gameId;
    private final int recipeId;

    public RecipeRemovedEvent(int gameId, int recipeId) {
        this.gameId = gameId;
        this.recipeId = recipeId;
    }

    public int getGameId() {
        return gameId;
    }

    public int getRecipeId() {
        return recipeId;
    }

}
