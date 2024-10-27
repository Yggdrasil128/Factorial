package de.yggdrasil128.factorial.controller;

import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.itemQuantity.ItemQuantity;
import de.yggdrasil128.factorial.model.itemQuantity.ItemQuantityStandalone;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
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
        applyRelations(input, recipe);
        recipe = recipeService.create(recipe);
        return new RecipeStandalone(recipe);
    }

    private ItemQuantity createResoruce(ItemQuantityStandalone input) {
        return new ItemQuantity(itemService.get((int) input.getItemId()), input.getQuantity());
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
        applyBasics(input, recipe);
        applyRelations(input, recipe);
        return new RecipeStandalone(recipeService.update(recipe));
    }

    private static void applyBasics(RecipeStandalone input, Recipe recipe) {
        OptionalInputField.of(input.getName()).apply(recipe::setName);
        OptionalInputField.of(input.getDuration()).apply(recipe::setDuration);
        OptionalInputField.of(input.getCategory()).apply(recipe::setCategory);
    }

    private void applyRelations(RecipeStandalone input, Recipe recipe) {
        OptionalInputField.of(input.getIngredients()).map(this::createResoruce).apply(recipe::setIngredients);
        OptionalInputField.of(input.getProducts()).map(this::createResoruce).apply(recipe::setProducts);
        OptionalInputField.ofId((int) input.getIconId(), iconService::get).apply(recipe::setIcon);
        OptionalInputField.ofIds(input.getApplicableModifierIds(), recipeModifierService::get)
                .applyList(recipe::setApplicableModifiers);
        OptionalInputField.ofIds(input.getApplicableMachineIds(), machineService::get)
                .applyList(recipe::setApplicableMachines);
    }

    @DeleteMapping("/recipe")
    public void delete(int recipeId) {
        recipeService.delete(recipeId);
    }

}
