package de.yggdrasil128.factorial.model.productionstep;

import java.util.List;

import org.springframework.stereotype.Service;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;

@Service
public class ProductionStepService extends ModelService<ProductionStep, ProductionStepRepository> {

    private final MachineService machines;
    private final RecipeService recipies;
    private final RecipeModifierService recipeModifiers;

    public ProductionStepService(ProductionStepRepository repository, MachineService machines, RecipeService recipies,
        RecipeModifierService recipeModifiers) {
        super(repository);
        this.machines = machines;
        this.recipies = recipies;
        this.recipeModifiers = recipeModifiers;
    }

    public ProductionStep create(Factory factory, ProductionStepStandalone input) {
        Machine machine = machines.get(input.getMachineId());
        Recipe recipe = recipies.get(input.getRecipeId());
        List<RecipeModifier> modifiers = recipeModifiers.get(input.getModifierIds());
        return repository.save(input.with(factory, machine, recipe, modifiers));
    }

    public ProductionStep doImport(Factory factory, ProductionStepMigration entry) {
        GameVersion gameVersion = factory.getSave().getGameVersion();
        Machine machine = machines.get(gameVersion, entry.getMachineName());
        Recipe recipe = recipies.get(gameVersion, entry.getRecipeName());
        List<RecipeModifier> modifiers = recipeModifiers.get(gameVersion, entry.getModifierNames());
        return new ProductionStep(factory, machine, recipe, modifiers, entry.getMachineCount());
    }

}
