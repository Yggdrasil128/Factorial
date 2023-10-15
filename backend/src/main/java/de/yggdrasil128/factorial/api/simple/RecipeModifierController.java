package de.yggdrasil128.factorial.api.simple;

import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.gameversion.GameVersionService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierInput;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierOutput;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import de.yggdrasil128.factorial.model.save.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeModifierController {

    private final GameVersionService gameVersionService;
    private final RecipeModifierService recipeModifierService;
    private final SaveService saveService;

    @Autowired
    public RecipeModifierController(GameVersionService gameVersionService, RecipeModifierService recipeModifierService,
                                    SaveService saveService) {
        this.gameVersionService = gameVersionService;
        this.recipeModifierService = recipeModifierService;
        this.saveService = saveService;
    }

    @PostMapping("/save/recipeModifiers")
    public RecipeModifierOutput createFromSave(int saveId, @RequestBody RecipeModifierInput input) {
        return create(saveService.get(saveId).getGameVersion(), input);
    }

    @PostMapping("/gameVersion/recipeModifiers")
    public RecipeModifierOutput create(int gameVersionId, @RequestBody RecipeModifierInput input) {
        return create(gameVersionService.get(gameVersionId), input);
    }

    private RecipeModifierOutput create(GameVersion gameVersion, RecipeModifierInput input) {
        RecipeModifier recipeModifier = recipeModifierService.create(gameVersion, input);
        gameVersionService.addAttachedRecipeModifier(gameVersion, recipeModifier);
        return new RecipeModifierOutput(recipeModifier);
    }

    @GetMapping("/save/recipeModifiers")
    public List<RecipeModifierOutput> retrieveAllFromSave(int saveId) {
        return retrieveAll(saveService.get(saveId).getGameVersion());
    }

    @GetMapping("/gameVersion/recipeModifiers")
    public List<RecipeModifierOutput> retrieveAll(int gameVersionId) {
        return retrieveAll(gameVersionService.get(gameVersionId));
    }

    private static List<RecipeModifierOutput> retrieveAll(GameVersion gameVersion) {
        return gameVersion.getRecipeModifiers().stream().map(RecipeModifierOutput::new).toList();
    }

    @GetMapping("/recipeModifier")
    public RecipeModifierOutput retrieve(int recipeModifierId) {
        return new RecipeModifierOutput(recipeModifierService.get(recipeModifierId));
    }

    @PatchMapping("/recipeModifier")
    public RecipeModifierOutput update(int recipeModifierId, @RequestBody RecipeModifierInput input) {
        return new RecipeModifierOutput(recipeModifierService.update(recipeModifierId, input));
    }

    @DeleteMapping("/recipeModifier")
    public void delete(int recipeModifierId) {
        recipeModifierService.delete(recipeModifierId);
    }

}
