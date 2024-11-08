package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {

    private final GameService gameService;
    private final RecipeService recipeService;

    @Autowired
    public RecipeController(GameService gameService, RecipeService recipeService) {
        this.gameService = gameService;
        this.recipeService = recipeService;
    }

    @PostMapping("/game/recipes")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void create(int gameId, @RequestBody RecipeStandalone input) {
        recipeService.create(gameId, input);
    }

    @GetMapping("/game/recipes")
    public List<RecipeStandalone> retrieveAlL(int gameId) {
        return gameService.get(gameId).getRecipes().stream().map(RecipeStandalone::of).toList();
    }

    @GetMapping("/recipe")
    public RecipeStandalone retrieve(int recipeId) {
        return RecipeStandalone.of(recipeService.get(recipeId));
    }

    @PatchMapping("/recipe")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void update(int recipeId, @RequestBody RecipeStandalone input) {
        recipeService.update(recipeId, input);
    }

    @DeleteMapping("/recipe")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(int recipeId) {
        recipeService.delete(recipeId);
    }

}
