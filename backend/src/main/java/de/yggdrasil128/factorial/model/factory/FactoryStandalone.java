package de.yggdrasil128.factorial.model.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.ProductionLine;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.ProductionEntryStandalone;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record FactoryStandalone(int id,
                                @JsonProperty(access = READ_ONLY) int saveId,
                                Integer ordinal,
                                String name,
                                String description,
                                Object iconId,
                                @JsonProperty(access = READ_ONLY) List<ProductionEntryStandalone> inputs,
                                @JsonProperty(access = READ_ONLY) List<ProductionEntryStandalone> outputs) {

    public static FactoryStandalone of(Factory model, ProductionLine productionLine) {
        return of(model, External.FRONTEND,
                productionLine.getInputs().entrySet().stream().map(ProductionEntryStandalone::of).toList(),
                productionLine.getOutputs().entrySet().stream().map(ProductionEntryStandalone::of).toList());
    }

    public static FactoryStandalone of(Factory model, External destination) {
        return of(model, destination, Collections.emptyList(), Collections.emptyList());
    }

    private static FactoryStandalone of(Factory model, External destination, List<ProductionEntryStandalone> inputs,
                                        List<ProductionEntryStandalone> outputs) {
        return new FactoryStandalone(model.getId(), model.getSave().getId(), model.getOrdinal(), model.getName(),
                model.getDescription(), NamedModel.resolve(model.getIcon(), destination), inputs, outputs);
    }

}
