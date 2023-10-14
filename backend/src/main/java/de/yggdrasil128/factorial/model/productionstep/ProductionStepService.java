package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.gameversion.GameVersion;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public ProductionStep doImport(Factory factory, ProductionStepMigration input) {
        GameVersion gameVersion = factory.getSave().getGameVersion();
        Machine machine = machines.get(gameVersion, input.getMachineName());
        Recipe recipe = recipies.get(gameVersion, input.getRecipeName());
        List<RecipeModifier> modifiers = recipeModifiers.get(gameVersion, input.getModifierNames());
        return new ProductionStep(factory, machine, recipe, modifiers, input.getMachineCount());
    }

}
