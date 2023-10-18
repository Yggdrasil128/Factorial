package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

import java.util.Map;

public class ProductionStepThroughputs {

    public static ProductionStepThroughputs of(ProductionStep productionStep, Changelists changelists) {
        QuantityByChangelist machineCounts = MachineCounts.of(productionStep, changelists);
        EffectiveModifiers effectiveModifiers = EffectiveModifiers.of(productionStep, machineCounts);
        Map<Item, QuantityByChangelist> input = Throughputs.inputOf(productionStep, effectiveModifiers);
        Map<Item, QuantityByChangelist> output = Throughputs.outputOf(productionStep, effectiveModifiers);
        return new ProductionStepThroughputs(input, output, machineCounts);
    }

    private final Map<Item, QuantityByChangelist> input;
    private final Map<Item, QuantityByChangelist> output;
    private final QuantityByChangelist machineCount;

    public ProductionStepThroughputs(Map<Item, QuantityByChangelist> input, Map<Item, QuantityByChangelist> output,
                                     QuantityByChangelist machineCount) {
        this.input = input;
        this.output = output;
        this.machineCount = machineCount;
    }

    public Map<Item, QuantityByChangelist> getInput() {
        return input;
    }

    public Map<Item, QuantityByChangelist> getOutput() {
        return output;
    }

    public QuantityByChangelist getMachineCount() {
        return machineCount;
    }

}
