package de.yggdrasil128.factorial.model.recipe;

public class RecipeUpdatedEvent {

    private final Recipe recipe;

    public RecipeUpdatedEvent(Recipe recipe) {
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }

}
