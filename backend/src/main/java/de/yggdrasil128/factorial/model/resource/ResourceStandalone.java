package de.yggdrasil128.factorial.model.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.yggdrasil128.factorial.engine.Production;
import de.yggdrasil128.factorial.engine.ProductionStepThroughputs;
import de.yggdrasil128.factorial.engine.QuantityByChangelist;
import de.yggdrasil128.factorial.engine.ResourceContributions;
import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

public class ResourceStandalone {

    @JsonProperty(access = READ_ONLY)
    private int id;
    @JsonProperty(access = READ_ONLY)
    private int factoryId;
    private int ordinal;
    private Object item;
    private boolean imported;
    private boolean exported;
    @JsonProperty(access = READ_ONLY)
    private List<Object> producers;
    @JsonProperty(access = READ_ONLY)
    private QuantityByChangelist produced;
    @JsonProperty(access = READ_ONLY)
    private List<Object> consumers;
    @JsonProperty(access = READ_ONLY)
    private QuantityByChangelist consumed;

    public ResourceStandalone() {
    }

    public ResourceStandalone(Resource model, ResourceContributions contributions) {
        this(model, RelationRepresentation.ID);
        producers = contributions.getProducers().stream().map(producer -> resolve(producer)).toList();
        produced = contributions.getProduced();
        consumers = contributions.getConsumers().stream().map(consumer -> resolve(consumer)).toList();
        consumed = contributions.getConsumed();
    }

    private static Object resolve(Production production) {
        if (production instanceof ProductionStepThroughputs productionStepThroughputs) {
            return productionStepThroughputs.getProductionStepId();
        }
        throw new AssertionError("production not instance of ProductionStepThroughputs, but "
                + production.getClass().getCanonicalName());
    }

    public ResourceStandalone(Resource model, RelationRepresentation resolveStrategy) {
        id = model.getId();
        factoryId = model.getFactory().getId();
        ordinal = model.getOrdinal();
        item = NamedModel.resolve(model.getItem(), resolveStrategy);
        imported = model.isImported();
        exported = model.isExported();
    }

    public int getId() {
        return id;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    public List<Object> getProducers() {
        return producers;
    }

    public QuantityByChangelist getProduced() {
        return produced;
    }

    public List<Object> getConsumers() {
        return consumers;
    }

    public QuantityByChangelist getConsumed() {
        return consumed;
    }

}
