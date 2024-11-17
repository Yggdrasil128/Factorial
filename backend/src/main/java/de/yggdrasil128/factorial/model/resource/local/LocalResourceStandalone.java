package de.yggdrasil128.factorial.model.resource.local;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.Production;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.QuantityByChangelist;
import de.yggdrasil128.factorial.model.External;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public record LocalResourceStandalone(int id,
                                      @JsonProperty(access = READ_ONLY) int factoryId,
                                      @JsonProperty(access = READ_ONLY) int saveId,
                                      Integer ordinal,
                                      Object itemId,
                                      boolean importExport,
                                      @JsonProperty(access = READ_ONLY) List<Object> producerIds,
                                      @JsonProperty(access = READ_ONLY) QuantityByChangelist produced,
                                      @JsonProperty(access = READ_ONLY) List<Object> consumerIds,
                                      @JsonProperty(access = READ_ONLY) QuantityByChangelist consumed,
                                      @JsonProperty(access = READ_ONLY) QuantityByChangelist overProduced) {

    public static LocalResourceStandalone of(LocalResource model, ResourceContributions contributions) {
        return of(model, External.FRONTEND,
                contributions.getProducers().stream().map(LocalResourceStandalone::resolve).toList(),
                contributions.getProduced(),
                contributions.getConsumers().stream().map(LocalResourceStandalone::resolve).toList(),
                contributions.getConsumed(), contributions.getOverProduced());
    }

    private static Object resolve(Production production) {
        return production.getEntityId();
    }

    public static LocalResourceStandalone of(LocalResource model, External destination) {
        return of(model, destination, Collections.emptyList(), null, Collections.emptyList(), null, null);
    }

    private static LocalResourceStandalone of(LocalResource model, External destination, List<Object> producerIds,
                                              QuantityByChangelist produced, List<Object> consumerIds,
                                              QuantityByChangelist consumed, QuantityByChangelist overProduced) {
        return new LocalResourceStandalone(model.getId(), model.getFactory().getId(),
                model.getFactory().getSave().getId(), model.getOrdinal(),
                NamedModel.resolve(model.getItem(), destination), model.isImportExport(), producerIds, produced,
                consumerIds, consumed, overProduced);
    }

}
