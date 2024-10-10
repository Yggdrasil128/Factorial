package de.yggdrasil128.factorial.model.productionstep;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.machine.MachineOutput;
import de.yggdrasil128.factorial.model.recipe.RecipeOutput;

public class ProductionStepOutput {

    private final ProductionStep delegate;
    private final MachineOutput machine;
    private final RecipeOutput recipe;

    public ProductionStepOutput(ProductionStep delegate) {
        this.delegate = delegate;
        this.machine = new MachineOutput(delegate.getMachine());
        this.recipe = new RecipeOutput(delegate.getRecipe());
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

}
