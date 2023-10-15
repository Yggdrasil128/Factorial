package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeInput;
import de.yggdrasil128.factorial.model.recipe.RecipeOutput;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {

    private final GameVersionService gameVersionService;
    private final RecipeService recipeService;
    private final SaveService saveService;

    @Autowired
    public RecipeController(GameVersionService gameVersionService, RecipeService recipeService,
                            SaveService saveService) {
        this.gameVersionService = gameVersionService;
        this.recipeService = recipeService;
        this.saveService = saveService;
    }

    @PostMapping("/save/recipes")
    public RecipeOutput createFromSave(int saveId, @RequestBody RecipeInput input) {
        return create(saveService.get(saveId).getGameVersion(), input);
    }

    @PostMapping("/gameVersion/recipes")
    public RecipeOutput create(int gameVersionId, @RequestBody RecipeInput input) {
        return create(gameVersionService.get(gameVersionId), input);
    }

    private RecipeOutput create(GameVersion gameVersion, RecipeInput input) {
        Recipe recipe = recipeService.create(gameVersion, input);
        gameVersionService.addAttachedRecipe(gameVersion, recipe);
        return new RecipeOutput(recipe);
    }

    @GetMapping("/save/recipes")
    public List<RecipeOutput> retrieveAlLFromSave(int saveId) {
        return retrieveAll(saveService.get(saveId).getGameVersion());
    }

    @GetMapping("/gameVersion/recipes")
    public List<RecipeOutput> retrieveAlL(int gameVersionId) {
        return retrieveAll(gameVersionService.get(gameVersionId));
    }

    private static List<RecipeOutput> retrieveAll(GameVersion gameVersion) {
        return gameVersion.getRecipes().stream().map(RecipeOutput::new).toList();
    }

    @GetMapping("/recipe")
    public RecipeOutput retrieve(int recipeId) {
        return new RecipeOutput(recipeService.get(recipeId));
    }

    @PatchMapping("/recipe")
    public RecipeOutput update(int recipeId, @RequestBody RecipeInput input) {
        return new RecipeOutput(recipeService.update(recipeId, input));
    }

    @DeleteMapping("/recipe")
    public void delete(int recipeId) {
        recipeService.delete(recipeId);
    }

}
