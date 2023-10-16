package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.factory.Factory;
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
    private final RecipeService recipes;
    private final RecipeModifierService recipeModifiers;

    public ProductionStepService(ProductionStepRepository repository, MachineService machines, RecipeService recipes,
                                 RecipeModifierService recipeModifiers) {
        super(repository);
        this.machines = machines;
        this.recipes = recipes;
        this.recipeModifiers = recipeModifiers;
    }

    public ProductionStep create(Factory factory, ProductionStepInput input) {
        Machine machine = machines.get(input.getMachineId());
        Recipe recipe = recipes.get(input.getRecipeId());
        List<RecipeModifier> modifiers = OptionalInputField.ofIds(input.getModifierIds(), recipeModifiers::get).get();
        Fraction machineCount = null == input.getMachineCount() ? Fraction.ONE : input.getMachineCount();
        return repository.save(new ProductionStep(factory, machine, recipe, modifiers, machineCount));
    }

    public ProductionStep update(int id, ProductionStepInput input) {
        ProductionStep productionStep = get(id);
        OptionalInputField.ofId(input.getMachineId(), machines::get).apply(productionStep::setMachine);
        OptionalInputField.ofId(input.getRecipeId(), recipes::get).apply(productionStep::setRecipe);
        OptionalInputField.ofIds(input.getModifierIds(), recipeModifiers::get).apply(productionStep::setModifiers);
        if (null != input.getMachineCount()) {
            productionStep.setMachineCount(input.getMachineCount());
        }
        return repository.save(productionStep);
    }

}
