package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.engine.Changelists;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.ModelService;
import de.yggdrasil128.factorial.model.OptionalInputField;
import de.yggdrasil128.factorial.model.changelist.Changelist;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemService;
import de.yggdrasil128.factorial.model.machine.Machine;
import de.yggdrasil128.factorial.model.machine.MachineService;
import de.yggdrasil128.factorial.model.recipe.Recipe;
import de.yggdrasil128.factorial.model.recipe.RecipeService;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifier;
import de.yggdrasil128.factorial.model.recipemodifier.RecipeModifierService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProductionStepService extends ModelService<ProductionStep, ProductionStepRepository> {

    private final ItemService items;
    private final MachineService machines;
    private final RecipeService recipes;
    private final RecipeModifierService recipeModifiers;

    public ProductionStepService(ProductionStepRepository repository, ItemService items, MachineService machines,
                                 RecipeService recipes, RecipeModifierService recipeModifiers) {
        super(repository);
        this.items = items;
        this.machines = machines;
        this.recipes = recipes;
        this.recipeModifiers = recipeModifiers;
    }

    public ProductionStep create(Factory factory, ProductionStepInput input) {
        Machine machine = machines.get(input.getMachineId());
        Recipe recipe = recipes.get(input.getRecipeId());
        List<RecipeModifier> modifiers = OptionalInputField.ofIds(input.getModifierIds(), recipeModifiers::get)
                .asList();
        Fraction machineCount = null == input.getMachineCount() ? Fraction.ONE : input.getMachineCount();
        Set<Item> uncloggingInputs = OptionalInputField.ofIds(input.getUncloggingInputIds(), items::get).asSet();
        Set<Item> uncloggingOutputs = OptionalInputField.ofIds(input.getUncloggingOutputIds(), items::get).asSet();
        return repository.save(new ProductionStep(factory, machine, recipe, modifiers, machineCount, uncloggingInputs,
                uncloggingOutputs));
    }

    public ProductionStep update(int id, ProductionStepInput input) {
        ProductionStep productionStep = get(id);
        OptionalInputField.ofId(input.getMachineId(), machines::get).apply(productionStep::setMachine);
        OptionalInputField.ofId(input.getRecipeId(), recipes::get).apply(productionStep::setRecipe);
        OptionalInputField.ofIds(input.getModifierIds(), recipeModifiers::get).applyList(productionStep::setModifiers);
        if (null != input.getMachineCount()) {
            productionStep.setMachineCount(input.getMachineCount());
        }
        OptionalInputField.ofIds(input.getUncloggingInputIds(), items::get)
                .applySet(productionStep::setUncloggingInputs);
        OptionalInputField.ofIds(input.getUncloggingOutputIds(), items::get)
                .applySet(productionStep::setUncloggingOutputs);
        return repository.save(productionStep);
    }

    public void applyChange(ProductionStep productionStep, Fraction change) {
        productionStep.setMachineCount(productionStep.getMachineCount().add(change));
        repository.save(productionStep);
    }

    public ProductionStep applyPrimaryChangelist(int id) {
        ProductionStep productionStep = get(id);
        Changelist primary = Changelists.getPrimary(productionStep.getFactory().getSave());
        Fraction change = primary.getProductionStepChanges().get(productionStep);
        if (null != change) {
            applyChange(productionStep, change);
        }
        return productionStep;
    }

}
