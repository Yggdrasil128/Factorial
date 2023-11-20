package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.item.ItemOutput;
import de.yggdrasil128.factorial.model.machine.MachineOutput;
import de.yggdrasil128.factorial.model.recipe.RecipeOutput;

import java.util.List;

public class ProductionStepOutput {

    private final ProductionStep delegate;
    private final MachineOutput machine;
    private final RecipeOutput recipe;
    private List<ItemOutput> uncloggingInputs;
    private List<ItemOutput> uncloggingOutputs;

    public ProductionStepOutput(ProductionStep delegate) {
        this.delegate = delegate;
        this.machine = new MachineOutput(delegate.getMachine());
        this.recipe = new RecipeOutput(delegate.getRecipe());
        uncloggingInputs = delegate.getUncloggingInputs().stream().map(ItemOutput::new).toList();
        uncloggingOutputs = delegate.getUncloggingOutputs().stream().map(ItemOutput::new).toList();
    }

    public int getId() {
        return delegate.getId();
    }

    public int getFactoryId() {
        return delegate.getFactory().getId();
    }

    public Fraction getMachineCount() {
        return delegate.getMachineCount();
    }

    public MachineOutput getMachine() {
        return machine;
    }

    public RecipeOutput getRecipe() {
        return recipe;
    }

    public List<ItemOutput> getUncloggingInputs() {
        return uncloggingInputs;
    }

    public List<ItemOutput> getUncloggingOutputs() {
        return uncloggingOutputs;
    }

}
