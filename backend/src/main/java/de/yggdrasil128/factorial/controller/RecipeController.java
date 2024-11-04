package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.recipe.*;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {

    private final GameService gameService;
    private final IconService iconService;
    private final ItemService itemService;
    private final RecipeService recipeService;
    private final RecipeModifierService recipeModifierService;
    private final MachineService machineService;

    @Autowired
    public RecipeController(GameService gameService, IconService iconService, ItemService itemService,
                            RecipeService recipeService, RecipeModifierService recipeModifierService,
                            MachineService machineService) {
        this.gameService = gameService;
        this.iconService = iconService;
        this.itemService = itemService;
        this.recipeService = recipeService;
        this.recipeModifierService = recipeModifierService;
        this.machineService = machineService;
    }

    @PostMapping("/game/recipes")
    public RecipeStandalone create(int gameId, @RequestBody RecipeStandalone input) {
        Game game = gameService.get(gameId);
        Recipe recipe = new Recipe(game, input);
        applyRelations(input, recipe);
        recipe = recipeService.create(recipe);
        return new RecipeStandalone(recipe);
    }

    private ItemQuantity createResoruce(ItemQuantityStandalone input) {
        return new ItemQuantity(itemService.get((int) input.itemId()), input.quantity());
    }

    @GetMapping("/game/recipes")
    public List<RecipeStandalone> retrieveAlL(int gameId) {
        return gameService.get(gameId).getRecipes().stream().map(RecipeStandalone::new).toList();
    }

    @GetMapping("/recipe")
    public RecipeStandalone retrieve(int recipeId) {
        return new RecipeStandalone(recipeService.get(recipeId));
    }

    @PatchMapping("/recipe")
    public RecipeStandalone update(int recipeId, @RequestBody RecipeStandalone input) {
        Recipe recipe = recipeService.get(recipeId);
        recipe.applyBasics(input);
        applyRelations(input, recipe);
        return new RecipeStandalone(recipeService.update(recipe));
    }

    private void applyRelations(RecipeStandalone input, Recipe recipe) {
        OptionalInputField.of(input.ingredients()).map(this::createResoruce).apply(recipe::setIngredients);
        OptionalInputField.of(input.products()).map(this::createResoruce).apply(recipe::setProducts);
        OptionalInputField.ofId(input.iconId(), iconService::get).apply(recipe::setIcon);
        OptionalInputField.ofIds(input.applicableModifierIds(), recipeModifierService::get)
                .applyList(recipe::setApplicableModifiers);
        OptionalInputField.ofIds(input.applicableMachineIds(), machineService::get)
                .applyList(recipe::setApplicableMachines);
    }

    @DeleteMapping("/recipe")
    public void delete(int recipeId) {
        recipeService.delete(recipeId);
    }

}
