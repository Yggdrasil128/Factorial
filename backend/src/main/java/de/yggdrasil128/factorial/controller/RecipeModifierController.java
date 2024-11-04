package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeModifierController {

    private final GameService gameService;
    private final IconService iconService;
    private final RecipeModifierService recipeModifierService;

    @Autowired
    public RecipeModifierController(GameService gameService, IconService iconService,
                                    RecipeModifierService recipeModifierService) {
        this.gameService = gameService;
        this.iconService = iconService;
        this.recipeModifierService = recipeModifierService;
    }

    @PostMapping("/game/recipeModifiers")
    public RecipeModifierStandalone create(int gameId, @RequestBody RecipeModifierStandalone input) {
        Game game = gameService.get(gameId);
        RecipeModifier recipeModifier = new RecipeModifier(game, input);
        applyRelations(input, recipeModifier);
        recipeModifier = recipeModifierService.create(recipeModifier);
        gameService.addAttachedRecipeModifier(game, recipeModifier);
        return RecipeModifierStandalone.of(recipeModifier);
    }

    @GetMapping("/game/recipeModifiers")
    public List<RecipeModifierStandalone> retrieveAll(int gameId) {
        return gameService.get(gameId).getRecipeModifiers().stream().map(RecipeModifierStandalone::of)
                .toList();
    }

    @GetMapping("/recipeModifier")
    public RecipeModifierStandalone retrieve(int recipeModifierId) {
        return RecipeModifierStandalone.of(recipeModifierService.get(recipeModifierId));
    }

    @PatchMapping("/recipeModifier")
    public RecipeModifierStandalone update(int recipeModifierId, @RequestBody RecipeModifierStandalone input) {
        RecipeModifier recipeModifier = recipeModifierService.get(recipeModifierId);
        recipeModifier.applyBasics(input);
        applyRelations(input, recipeModifier);
        return RecipeModifierStandalone.of(recipeModifierService.update(recipeModifier));
    }

    private void applyRelations(RecipeModifierStandalone input, RecipeModifier recipeModifier) {
        OptionalInputField.ofId(input.iconId(), iconService::get).apply(recipeModifier::setIcon);
    }

    @DeleteMapping("/recipeModifier")
    public void delete(int recipeModifierId) {
        recipeModifierService.delete(recipeModifierId);
    }

}
