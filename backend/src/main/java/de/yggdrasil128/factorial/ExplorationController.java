package de.yggdrasil128.factorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.factory.FactoryService;
import de.yggdrasil128.factorial.model.factory.FactoryStandalone;
import de.yggdrasil128.factorial.model.game.Game;
import de.yggdrasil128.factorial.model.game.GameService;
import de.yggdrasil128.factorial.model.game.GameStandalone;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.gameversion.GameVersionStandalone;
import de.yggdrasil128.factorial.model.icon.Icon;
import de.yggdrasil128.factorial.model.icon.IconService;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.item.ItemStandalone;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.machine.MachineStandalone;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepService;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepStandalone;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipe.RecipeStandalone;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierStandalone;
import de.yggdrasil128.factorial.model.save.Save;
import de.yggdrasil128.factorial.model.save.SaveService;
import de.yggdrasil128.factorial.model.save.SaveStandalone;

/**
 * A controller I used for exploring Spring during development.
 * 
 * @author Izruo
 */
@RestController
@RequestMapping("/noapi/exploration")
public class ExplorationController {

    private final GameService games;
    private final GameVersionService gameVersions;
    private final ItemService items;
    private final RecipeService recipies;
    private final RecipeModifierService recipeModifiers;
    private final MachineService machines;
    private final SaveService saves;
    private final FactoryService factories;
    private final ProductionStepService productionSteps;
    private final IconService icons;

    @Autowired
    public ExplorationController(GameService games, GameVersionService gameVersions, ItemService items,
        RecipeService recipies, RecipeModifierService recipeModifiers, MachineService machines, SaveService saves,
        FactoryService factories, ProductionStepService productionSteps, IconService icons) {
        this.games = games;
        this.gameVersions = gameVersions;
        this.items = items;
        this.recipies = recipies;
        this.recipeModifiers = recipeModifiers;
        this.machines = machines;
        this.saves = saves;
        this.factories = factories;
        this.productionSteps = productionSteps;
        this.icons = icons;
    }

    @GetMapping("/game")
    public Game getGame(int id) {
        return games.get(id);
    }

    @PostMapping("/game")
    public Game postGame(@RequestBody GameStandalone game) {
        return games.create(game);
    }

    @DeleteMapping("/game")
    public void deleteGame(int id) {
        games.delete(id);
    }

    @GetMapping("/gameVersion")
    public GameVersion getGameVersion(int id) {
        return gameVersions.get(id);
    }

    @PostMapping("/gameVersion")
    public GameVersion postGameVersion(@RequestBody GameVersionStandalone gameVersion, int gameId) {
        return games.addGameVersion(gameId, gameVersion);
    }

    @DeleteMapping("/gameVersion")
    public void deleteGameVersion(int id) {
        gameVersions.delete(id);
    }

    @GetMapping("/item")
    public Item getItem(int id) {
        return items.get(id);
    }

    @PostMapping("/item")
    public Item postItem(@RequestBody ItemStandalone item, int gameVersionId) {
        return gameVersions.addItem(gameVersionId, item);
    }

    @DeleteMapping("/item")
    public void deleteItem(int id) {
        items.delete(id);
    }

    @GetMapping("/recipe")
    public Recipe getRecipe(int id) {
        return recipies.get(id);
    }

    @PostMapping("/recipe")
    public Recipe postRecipe(@RequestBody RecipeStandalone recipe, int gameVersionId) {
        return gameVersions.addRecipe(gameVersionId, recipe);
    }

    @DeleteMapping("/recipe")
    public void deleteRecipe(int id) {
        recipies.delete(id);
    }

    @GetMapping("/recipeModifier")
    public RecipeModifier getRecipeModifier(int id) {
        return recipeModifiers.get(id);
    }

    @PostMapping("/recipeModifier")
    public RecipeModifier postRecipeModifier(@RequestBody RecipeModifierStandalone recipeModifier, int gameVersionId) {
        return gameVersions.addRecipeModifier(gameVersionId, recipeModifier);
    }

    @DeleteMapping("/recipeModifier")
    public void deleteRecipeModifier(int id) {
        recipeModifiers.delete(id);
    }

    @GetMapping("/machine")
    public Machine getMachine(int id) {
        return machines.get(id);
    }

    @PostMapping("/machine")
    public Machine postMachine(@RequestBody MachineStandalone machine, int gameVersionId) {
        return gameVersions.addMachine(gameVersionId, machine);
    }

    @DeleteMapping("/machine")
    public void deleteMachine(int id) {
        machines.delete(id);
    }

    @GetMapping("/save")
    public Save getSave(int id) {
        return saves.get(id);
    }

    @PostMapping("save")
    public Save postSave(@RequestBody SaveStandalone save, int gameVersionId) {
        return saves.create(gameVersionId, save);
    }

    @DeleteMapping("save")
    public void deleteSave(int id) {
        saves.delete(id);
    }

    @GetMapping("/factory")
    public Factory getFactory(int id) {
        return factories.get(id);
    }

    @PostMapping("/factory")
    public Factory postFactory(@RequestBody FactoryStandalone factory, int saveId) {
        return saves.addFactory(saveId, factory);
    }

    @DeleteMapping("/factory")
    public void deleteFactory(int id) {
        factories.delete(id);
    }

    @GetMapping("/productionStep")
    public ProductionStep getProductionStep(int id) {
        return productionSteps.get(id);
    }

    @PostMapping("/productionStep")
    public ProductionStep postProductionStep(@RequestBody ProductionStepStandalone productionStep, int factoryId) {
        return factories.addProductionStep(factoryId, productionStep);
    }

    @DeleteMapping("/productionStep")
    public void deleteProductionStep(int id) {
        productionSteps.delete(id);
    }

    @GetMapping("/icon")
    public Icon getIcon(int id) {
        return icons.get(id);
    }

    @PostMapping("/icon")
    public Icon postIcon(@RequestBody Icon icon) {
        return icons.create(icon);
    }

    @DeleteMapping("/icon")
    public void deleteIcon(int id) {
        icons.delete(id);
    }

}
