package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
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

    private final GameVersionService gameVersionService;
    private final IconService iconService;
    private final RecipeModifierService recipeModifierService;

    @Autowired
    public RecipeModifierController(GameVersionService gameVersionService, IconService iconService,
                                    RecipeModifierService recipeModifierService) {
        this.gameVersionService = gameVersionService;
        this.iconService = iconService;
        this.recipeModifierService = recipeModifierService;
    }

    @PostMapping("/gameVersion/recipeModifiers")
    public RecipeModifierStandalone create(int gameVersionId, @RequestBody RecipeModifierStandalone input) {
        GameVersion gameVersion = gameVersionService.get(gameVersionId);
        RecipeModifier recipeModifier = new RecipeModifier(gameVersion, input);
        applyRelations(input, recipeModifier);
        recipeModifier = recipeModifierService.create(recipeModifier);
        gameVersionService.addAttachedRecipeModifier(gameVersion, recipeModifier);
        return RecipeModifierStandalone.of(recipeModifier);
    }

    @GetMapping("/gameVersion/recipeModifiers")
    public List<RecipeModifierStandalone> retrieveAll(int gameVersionId) {
        return gameVersionService.get(gameVersionId).getRecipeModifiers().stream().map(RecipeModifierStandalone::of)
                .toList();
    }

    @GetMapping("/recipeModifier")
    public RecipeModifierStandalone retrieve(int recipeModifierId) {
        return RecipeModifierStandalone.of(recipeModifierService.get(recipeModifierId));
    }

    @PatchMapping("/recipeModifier")
    public RecipeModifierStandalone update(int recipeModifierId, @RequestBody RecipeModifierStandalone input) {
        RecipeModifier recipeModifier = recipeModifierService.get(recipeModifierId);
        applyBasics(input, recipeModifier);
        applyRelations(input, recipeModifier);
        return RecipeModifierStandalone.of(recipeModifierService.update(recipeModifier));
    }

    private static void applyBasics(RecipeModifierStandalone input, RecipeModifier recipeModifier) {
        OptionalInputField.of(input.name()).apply(recipeModifier::setName);
        OptionalInputField.of(input.description()).apply(recipeModifier::setDescription);
        OptionalInputField.of(input.durationMultiplier()).apply(recipeModifier::setDurationMultiplier);
        OptionalInputField.of(input.inputQuantityMultiplier()).apply(recipeModifier::setInputQuantityMultiplier);
        OptionalInputField.of(input.outputQuantityMultiplier()).apply(recipeModifier::setOutputQuantityMultiplier);
    }

    private void applyRelations(RecipeModifierStandalone input, RecipeModifier recipeModifier) {
        OptionalInputField.ofId((int) input.iconId(), iconService::get).apply(recipeModifier::setIcon);
    }

    @DeleteMapping("/recipeModifier")
    public void delete(int recipeModifierId) {
        recipeModifierService.delete(recipeModifierId);
    }

}
