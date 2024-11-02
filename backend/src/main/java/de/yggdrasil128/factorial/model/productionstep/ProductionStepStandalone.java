package de.yggdrasil128.factorial.model.productionstep;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.QuantityByChangelist;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.ProductionEntryStandalone;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record ProductionStepStandalone(@JsonProperty(access = READ_ONLY) int id,
                                       @JsonProperty(access = READ_ONLY) int factoryId,
                                       Object machineId,
                                       Object recipeId,
                                       List<Object> modifierIds,
                                       Fraction machineCount,
                                       QuantityByChangelist machineCounts,
                                       @JsonProperty(access = READ_ONLY) List<ProductionEntryStandalone> inputs,
                                       @JsonProperty(access = READ_ONLY) List<ProductionEntryStandalone> outputs) {

    public static ProductionStepStandalone of(ProductionStep model, ProductionStepThroughputs throughputs) {
        return of(model, External.FRONTEND, throughputs.getMachineCounts(),
                throughputs.getInputs().entrySet().stream().map(ProductionEntryStandalone::of).toList(),
                throughputs.getOutputs().entrySet().stream().map(ProductionEntryStandalone::of).toList());
    }

    public static ProductionStepStandalone of(ProductionStep model, External destination) {
        return of(model, destination, null, Collections.emptyList(), Collections.emptyList());
    }

    private static ProductionStepStandalone
            of(ProductionStep model, External destination, QuantityByChangelist machineCounts,
               List<ProductionEntryStandalone> inputs, List<ProductionEntryStandalone> outputs) {
        return new ProductionStepStandalone(model.getId(), model.getFactory().getId(),
                NamedModel.resolve(model.getMachine(), destination), NamedModel.resolve(model.getRecipe(), destination),
                NamedModel.resolve(model.getModifiers(), destination), model.getMachineCount(), machineCounts, inputs,
                outputs);
    }

}
