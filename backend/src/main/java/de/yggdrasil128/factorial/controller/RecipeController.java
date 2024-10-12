package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.ResourceStandalone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {

    private final GameVersionService gameVersionService;
    private final IconService iconService;
    private final ItemService itemService;
    private final RecipeService recipeService;
    private final RecipeModifierService recipeModifierService;
    private final MachineService machineService;

    @Autowired
    public RecipeController(GameVersionService gameVersionService, IconService iconService, ItemService itemService,
                            RecipeService recipeService, RecipeModifierService recipeModifierService,
                            MachineService machineService) {
        this.gameVersionService = gameVersionService;
        this.iconService = iconService;
        this.itemService = itemService;
        this.recipeService = recipeService;
        this.recipeModifierService = recipeModifierService;
        this.machineService = machineService;
    }

    @PostMapping("/gameVersion/recipes")
    public RecipeStandalone create(int gameVersionId, @RequestBody RecipeStandalone input) {
        GameVersion gameVersion = gameVersionService.get(gameVersionId);
        Recipe recipe = new Recipe(gameVersion, input);
        OptionalInputField.ofId((int) input.getIcon(), iconService::get).apply(recipe::setIcon);
        OptionalInputField.of(input.getInput()).map(this::createResoruce).apply(recipe::setInput);
        OptionalInputField.of(input.getOutput()).map(this::createResoruce).apply(recipe::setOutput);
        OptionalInputField.ofIds(input.getApplicableModifiers(), recipeModifierService::get)
                .applyList(recipe::setApplicableModifiers);
        OptionalInputField.ofIds(input.getApplicableMachines(), machineService::get)
                .applyList(recipe::setApplicableMachines);
        recipe = recipeService.create(recipe);
        return new RecipeStandalone(recipe);
    }

    private Resource createResoruce(ResourceStandalone input) {
        return new Resource(itemService.get((int) input.getItem()), input.getQuantity());
    }

    @GetMapping("/gameVersion/recipes")
    public List<RecipeStandalone> retrieveAlL(int gameVersionId) {
        return gameVersionService.get(gameVersionId).getRecipes().stream().map(RecipeStandalone::new).toList();
    }

    @GetMapping("/recipe")
    public RecipeStandalone retrieve(int recipeId) {
        return new RecipeStandalone(recipeService.get(recipeId));
    }

    @PatchMapping("/recipe")
    public RecipeStandalone update(int recipeId, @RequestBody RecipeStandalone input) {
        Recipe recipe = recipeService.get(recipeId);
        OptionalInputField.of(input.getName()).apply(recipe::setName);
        OptionalInputField.ofId((int) input.getIcon(), iconService::get).apply(recipe::setIcon);
        OptionalInputField.of(input.getInput()).map(this::createResoruce).apply(recipe::setInput);
        OptionalInputField.of(input.getOutput()).map(this::createResoruce).apply(recipe::setOutput);
        OptionalInputField.of(input.getDuration()).apply(recipe::setDuration);
        OptionalInputField.ofIds(input.getApplicableModifiers(), recipeModifierService::get)
                .applyList(recipe::setApplicableModifiers);
        OptionalInputField.ofIds(input.getApplicableMachines(), machineService::get)
                .applyList(recipe::setApplicableMachines);
        OptionalInputField.of(input.getCategory()).apply(recipe::setCategory);
        return new RecipeStandalone(recipeService.update(recipe));
    }

    @DeleteMapping("/recipe")
    public void delete(int recipeId) {
        recipeService.delete(recipeId);
    }

}
