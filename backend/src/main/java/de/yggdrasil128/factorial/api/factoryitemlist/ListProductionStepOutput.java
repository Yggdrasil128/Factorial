package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.QuantityByChangelist;
import de.yggdrasil128.factorial.model.machine.MachineOutput;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;
import de.yggdrasil128.factorial.model.productionstep.ProductionStepOutput;
import de.yggdrasil128.factorial.model.recipe.RecipeOutput;

import java.util.List;

public class ListProductionStepOutput {

    private final ProductionStepOutput delegate;
    private final List<ThroughputOutput> input;
    private final List<ThroughputOutput> output;
    private final QuantityByChangelist machineCount;

    public ListProductionStepOutput(ProductionStep delegate, ProductionStepThroughputs throughputs) {
        this.delegate = new ProductionStepOutput(delegate);
        input = throughputs.getInput().entrySet().stream()
                .map(entry -> new ThroughputOutput(entry.getKey(), entry.getValue())).toList();
        output = throughputs.getOutput().entrySet().stream()
                .map(entry -> new ThroughputOutput(entry.getKey(), entry.getValue())).toList();
        machineCount = throughputs.getMachineCount();
    }

    public int getId() {
        return delegate.getId();
    }

    public int getFactoryId() {
        return delegate.getFactoryId();
    }

    public MachineOutput getMachine() {
        return delegate.getMachine();
    }

    public RecipeOutput getRecipe() {
        return delegate.getRecipe();
    }

    public List<ThroughputOutput> getInput() {
        return input;
    }

    public List<ThroughputOutput> getOutput() {
        return output;
    }

    public QuantityByChangelist getMachineCount() {
        return machineCount;
    }

}
