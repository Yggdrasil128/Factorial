package de.yggdrasil128.factorial.model.recipemodifier;

public class RecipeModifierUpdatedEvent {

    private final RecipeModifier recipeModifier;

    public RecipeModifierUpdatedEvent(RecipeModifier recipeModifier) {
        this.recipeModifier = recipeModifier;
    }

    public RecipeModifier getRecipeModifier() {
        return recipeModifier;
    }

}
