package de.yggdrasil128.factorial.model.resource.global;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.Production;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.External;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.QuantityByChangelist;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record GlobalResourceStandalone(int id,
                                       @JsonProperty(access = READ_ONLY) int saveId,
                                       Integer ordinal,
                                       Object itemId,
                                       @JsonProperty(access = READ_ONLY) List<Object> producerIds,
                                       @JsonProperty(access = READ_ONLY) QuantityByChangelist produced,
                                       @JsonProperty(access = READ_ONLY) List<Object> consumerIds,
                                       @JsonProperty(access = READ_ONLY) QuantityByChangelist consumed,
                                       @JsonProperty(access = READ_ONLY) QuantityByChangelist overProduced) {

    public static GlobalResourceStandalone of(GlobalResource model, ResourceContributions contributions) {
        return of(model, External.FRONTEND,
                contributions.getProducers().stream().map(GlobalResourceStandalone::resolve).toList(),
                contributions.getProduced(),
                contributions.getConsumers().stream().map(GlobalResourceStandalone::resolve).toList(),
                contributions.getConsumed(), contributions.getOverProduced());
    }

    private static Object resolve(Production production) {
        return production.getEntityId();
    }

    public static GlobalResourceStandalone of(GlobalResource model, External destination) {
        return of(model, destination, Collections.emptyList(), null, Collections.emptyList(), null, null);
    }

    private static GlobalResourceStandalone of(GlobalResource model, External destination, List<Object> producerIds,
                                               QuantityByChangelist produced, List<Object> consumerIds,
                                               QuantityByChangelist consumed, QuantityByChangelist overProduced) {
        return new GlobalResourceStandalone(model.getId(), model.getSave().getId(), model.getOrdinal(),
                NamedModel.resolve(model.getItem(), destination), producerIds, produced, consumerIds, consumed,
                overProduced);
    }

}
