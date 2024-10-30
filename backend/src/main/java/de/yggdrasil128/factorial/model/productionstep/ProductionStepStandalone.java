package de.yggdrasil128.factorial.model.productionstep;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.ProductionEntryStandalone;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record ProductionStepStandalone(@JsonProperty(access = READ_ONLY) int id,
                                       @JsonProperty(access = READ_ONLY) int factoryId,
                                       Object machineId,
                                       Object recipeId,
                                       List<Object> modifierIds,
                                       Fraction machineCount,
                                       @JsonProperty(access = READ_ONLY) List<ProductionEntryStandalone> inputs,
                                       @JsonProperty(access = READ_ONLY) List<ProductionEntryStandalone> outputs) {

    public static ProductionStepStandalone of(ProductionStep model, ProductionStepThroughputs throughputs) {
        return of(model, RelationRepresentation.ID,
                throughputs.getInputs().entrySet().stream().map(ProductionEntryStandalone::of).toList(),
                throughputs.getOutputs().entrySet().stream().map(ProductionEntryStandalone::of).toList());
    }

    public static ProductionStepStandalone of(ProductionStep model, RelationRepresentation resolveStrategy) {
        return of(model, resolveStrategy, Collections.emptyList(), Collections.emptyList());
    }

    private static ProductionStepStandalone of(ProductionStep model, RelationRepresentation resolveStrategy,
                                               List<ProductionEntryStandalone> inputs,
                                               List<ProductionEntryStandalone> outputs) {
        return new ProductionStepStandalone(model.getId(), model.getFactory().getId(),
                NamedModel.resolve(model.getMachine(), resolveStrategy),
                NamedModel.resolve(model.getRecipe(), resolveStrategy),
                NamedModel.resolve(model.getModifiers(), resolveStrategy), model.getMachineCount(), inputs, outputs);
    }

}
