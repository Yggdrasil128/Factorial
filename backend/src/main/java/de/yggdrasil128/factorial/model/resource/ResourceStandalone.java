package de.yggdrasil128.factorial.model.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.Production;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.QuantityByChangelist;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.External;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record ResourceStandalone(@JsonProperty(access = READ_ONLY) int id,
                                 @JsonProperty(access = READ_ONLY) int factoryId,
                                 @JsonProperty(access = READ_ONLY) int saveId,
                                 Integer ordinal,
                                 Object itemId,
                                 boolean imported,
                                 boolean exported,
                                 @JsonProperty(access = READ_ONLY) List<Object> producerIds,
                                 @JsonProperty(access = READ_ONLY) QuantityByChangelist produced,
                                 @JsonProperty(access = READ_ONLY) List<Object> consumerIds,
                                 @JsonProperty(access = READ_ONLY) QuantityByChangelist consumed,
                                 @JsonProperty(access = READ_ONLY) QuantityByChangelist overProduced) {

    public static ResourceStandalone of(Resource model, ResourceContributions contributions) {
        return of(model, External.FRONTEND,
                contributions.getProducers().stream().map(producer -> resolve(producer)).toList(),
                contributions.getProduced(),
                contributions.getConsumers().stream().map(consumer -> resolve(consumer)).toList(),
                contributions.getConsumed(), contributions.getOverProduced());
    }

    private static Object resolve(Production production) {
        if (production instanceof ProductionStepThroughputs productionStepThroughputs) {
            return productionStepThroughputs.getProductionStepId();
        }
        throw new AssertionError("production not instance of ProductionStepThroughputs, but "
                + production.getClass().getCanonicalName());
    }

    public static ResourceStandalone of(Resource model, External destination) {
        return of(model, destination, Collections.emptyList(), null, Collections.emptyList(), null, null);
    }

    private static ResourceStandalone of(Resource model, External destination, List<Object> producerIds,
                                         QuantityByChangelist produced, List<Object> consumerIds,
                                         QuantityByChangelist consumed, QuantityByChangelist overProduced) {
        return new ResourceStandalone(model.getId(), model.getFactory().getId(), model.getFactory().getSave().getId(),
                model.getOrdinal(), NamedModel.resolve(model.getItem(), destination), model.isImported(),
                model.isExported(), producerIds, produced, consumerIds, consumed, overProduced);
    }

}
