package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class RecipeController {

    private final AsyncHelper asyncHelper;
    private final GameService gameService;
    private final RecipeService recipeService;

    @Autowired
    public RecipeController(AsyncHelper asyncHelper, GameService gameService, RecipeService recipeService) {
        this.asyncHelper = asyncHelper;
        this.gameService = gameService;
        this.recipeService = recipeService;
    }

    @PostMapping("/game/recipes")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> create(int gameId, @RequestBody List<RecipeStandalone> input) {
        return asyncHelper.submit(result -> recipeService.create(gameId, input, result));
    }

    @GetMapping("/game/recipes")
    public List<RecipeStandalone> retrieveAlL(int gameId) {
        return gameService.get(gameId).getRecipes().stream().map(RecipeStandalone::of).toList();
    }

    @GetMapping("/recipe")
    public RecipeStandalone retrieve(int recipeId) {
        return RecipeStandalone.of(recipeService.get(recipeId));
    }

    @PatchMapping("/recipes")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(@RequestBody List<RecipeStandalone> input) {
        return asyncHelper.submit(result -> recipeService.update(input, result));
    }

    @DeleteMapping("/recipes")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> delete(List<Integer> recipeIds) {
        return asyncHelper.submit(result -> recipeService.delete(recipeIds, result));
    }

}
