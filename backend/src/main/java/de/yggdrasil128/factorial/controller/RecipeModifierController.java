package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeModifierController {

    private final GameService gameService;
    private final RecipeModifierService recipeModifierService;

    @Autowired
    public RecipeModifierController(GameService gameService, RecipeModifierService recipeModifierService) {
        this.gameService = gameService;
        this.recipeModifierService = recipeModifierService;
    }

    @PostMapping("/game/recipeModifiers")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void create(int gameId, @RequestBody RecipeModifierStandalone input) {
        recipeModifierService.create(gameId, input);
    }

    @GetMapping("/game/recipeModifiers")
    public List<RecipeModifierStandalone> retrieveAll(int gameId) {
        return gameService.get(gameId).getRecipeModifiers().stream().map(RecipeModifierStandalone::of).toList();
    }

    @GetMapping("/recipeModifier")
    public RecipeModifierStandalone retrieve(int recipeModifierId) {
        return RecipeModifierStandalone.of(recipeModifierService.get(recipeModifierId));
    }

    @PatchMapping("/recipeModifier")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void update(int recipeModifierId, @RequestBody RecipeModifierStandalone input) {
        recipeModifierService.update(recipeModifierId, input);
    }

    @DeleteMapping("/recipeModifier")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(int recipeModifierId) {
        recipeModifierService.delete(recipeModifierId);
    }

}
