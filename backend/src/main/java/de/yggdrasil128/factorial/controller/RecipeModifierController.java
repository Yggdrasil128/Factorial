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
        return new RecipeModifierStandalone(recipeModifier);
    }

    @GetMapping("/gameVersion/recipeModifiers")
    public List<RecipeModifierStandalone> retrieveAll(int gameVersionId) {
        return gameVersionService.get(gameVersionId).getRecipeModifiers().stream().map(RecipeModifierStandalone::new)
                .toList();
    }

    @GetMapping("/recipeModifier")
    public RecipeModifierStandalone retrieve(int recipeModifierId) {
        return new RecipeModifierStandalone(recipeModifierService.get(recipeModifierId));
    }

    @PatchMapping("/recipeModifier")
    public RecipeModifierStandalone update(int recipeModifierId, @RequestBody RecipeModifierStandalone input) {
        RecipeModifier recipeModifier = recipeModifierService.get(recipeModifierId);
        applyBasics(input, recipeModifier);
        applyRelations(input, recipeModifier);
        return new RecipeModifierStandalone(recipeModifierService.update(recipeModifier));
    }

    private static void applyBasics(RecipeModifierStandalone input, RecipeModifier recipeModifier) {
        OptionalInputField.of(input.getName()).apply(recipeModifier::setName);
        OptionalInputField.of(input.getDescription()).apply(recipeModifier::setDescription);
        OptionalInputField.of(input.getDurationMultiplier()).apply(recipeModifier::setDurationMultiplier);
        OptionalInputField.of(input.getInputQuantityMultiplier()).apply(recipeModifier::setInputQuantityMultiplier);
        OptionalInputField.of(input.getOutputQuantityMultiplier()).apply(recipeModifier::setOutputQuantityMultiplier);
    }

    private void applyRelations(RecipeModifierStandalone input, RecipeModifier recipeModifier) {
        OptionalInputField.ofId((int) input.getIconId(), iconService::get).apply(recipeModifier::setIcon);
    }

    @DeleteMapping("/recipeModifier")
    public void delete(int recipeModifierId) {
        recipeModifierService.delete(recipeModifierId);
    }

}
