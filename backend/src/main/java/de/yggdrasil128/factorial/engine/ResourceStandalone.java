package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.NamedModel;
import de.yggdrasil128.factorial.model.RelationRepresentation;
import de.yggdrasil128.factorial.model.productionstep.ProductionStep;

import java.util.List;

public class ResourceStandalone {

    private Object item;
    private boolean imported;
    private boolean exported;
    private List<Object> producers;
    private QuantityByChangelist produced;
    private List<Object> consumers;
    private QuantityByChangelist consumed;

    public ResourceStandalone() {
    }

    public ResourceStandalone(Resource model) {
        this(model, RelationRepresentation.ID);
    }

    public ResourceStandalone(Resource model, RelationRepresentation resolveStrategy) {
        item = NamedModel.resolve(model.getItem(), resolveStrategy);
        imported = model.isImported();
        exported = model.isExported();
        producers = model.getProducers().stream().map(producer -> resolve(producer, resolveStrategy)).toList();
        produced = model.getProduced();
        consumers = model.getConsumers().stream().map(consumer -> resolve(consumer, resolveStrategy)).toList();
        consumed = model.getConsumed();
    }

    private static Object resolve(Production production, RelationRepresentation resolveStrategy) {
        if (production instanceof ProductionStepThroughputs productionStepThroughputs) {
            return ProductionStep.resolve(productionStepThroughputs.getDelegate(), resolveStrategy);
        }
        throw new AssertionError("production not instance of ProductionStepThroughputs, but "
                + production.getClass().getCanonicalName());
    }

    public Object getItem() {
        return item;
    }

    public boolean isImported() {
        return imported;
    }

    public boolean isExported() {
        return exported;
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
