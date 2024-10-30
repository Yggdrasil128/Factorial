package de.yggdrasil128.factorial.model.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.ProductionEntryStandalone;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record FactoryStandalone(@JsonProperty(access = READ_ONLY) int id,
                                @JsonProperty(access = READ_ONLY) int saveId,
                                int ordinal,
                                String name,
                                String description,
                                Object iconId,
                                List<Object> productionStepIds,
                                List<Object> resourceIds,
                                @JsonProperty(access = READ_ONLY) List<ProductionEntryStandalone> inputs,
                                @JsonProperty(access = READ_ONLY) List<ProductionEntryStandalone> outputs) {

    public static FactoryStandalone of(Factory model, ProductionLine productionLine) {
        return of(model, RelationRepresentation.ID,
                productionLine.getInputs().entrySet().stream().map(ProductionEntryStandalone::of).toList(),
                productionLine.getOutputs().entrySet().stream().map(ProductionEntryStandalone::of).toList());
    }

    public static FactoryStandalone of(Factory model, RelationRepresentation resolveStrategy) {
        return of(model, resolveStrategy, Collections.emptyList(), Collections.emptyList());
    }

    private static FactoryStandalone of(Factory model, RelationRepresentation resolveStrategy,
                                        List<ProductionEntryStandalone> inputs,
                                        List<ProductionEntryStandalone> outputs) {
        return new FactoryStandalone(model.getId(), model.getSave().getId(), model.getOrdinal(), model.getName(),
                model.getDescription(), NamedModel.resolve(model.getIcon(), resolveStrategy),
                RelationRepresentation.ID == resolveStrategy ? NamedModel.resolve(model.getProductionSteps(),
                        resolveStrategy, (productionStep, strategy) -> productionStep.getId())
                        : Collections.emptyList(),
                RelationRepresentation.ID == resolveStrategy
                        ? NamedModel.resolve(model.getResources(), resolveStrategy,
                                (resource, strategy) -> resource.getId())
                        : Collections.emptyList(),
                inputs, outputs);
    }

}
