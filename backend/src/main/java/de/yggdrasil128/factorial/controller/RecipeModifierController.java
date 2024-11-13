package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.AsyncHelper;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class RecipeModifierController {

    private final AsyncHelper asyncHelper;
    private final GameService gameService;
    private final RecipeModifierService recipeModifierService;

    @Autowired
    public RecipeModifierController(AsyncHelper asyncHelper, GameService gameService,
                                    RecipeModifierService recipeModifierService) {
        this.asyncHelper = asyncHelper;
        this.gameService = gameService;
        this.recipeModifierService = recipeModifierService;
    }

    @PostMapping("/game/recipeModifiers")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> create(int gameId, @RequestBody List<RecipeModifierStandalone> input) {
        return asyncHelper.submit(result -> recipeModifierService.create(gameId, input, result));
    }

    @GetMapping("/game/recipeModifiers")
    public List<RecipeModifierStandalone> retrieveAll(int gameId) {
        return gameService.get(gameId).getRecipeModifiers().stream().map(RecipeModifierStandalone::of).toList();
    }

    @GetMapping("/recipeModifier")
    public RecipeModifierStandalone retrieve(int recipeModifierId) {
        return RecipeModifierStandalone.of(recipeModifierService.get(recipeModifierId));
    }

    @PatchMapping("/recipeModifiers")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> update(@RequestBody List<RecipeModifierStandalone> input) {
        return asyncHelper.submit(result -> recipeModifierService.update(input, result));
    }

    @DeleteMapping("/recipeModifiers")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public CompletableFuture<Void> delete(List<Integer> recipeModifierIds) {
        return asyncHelper.submit(result -> recipeModifierService.delete(recipeModifierIds, result));
    }

}
