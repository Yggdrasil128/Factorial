package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.resource.Resource;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * For a single item, represents how much of it is produced and consumed within a {@link ProductionLine}.
 */
public class ResourceContributions {

    // we must not keep references to the respective entities here
    private final int resourceId;
    private final int itemId;
    private boolean imported;
    private boolean exported;
    private final Set<Production> producers = new HashSet<>();
    private final Set<Production> consumers = new HashSet<>();

    public ResourceContributions(Resource resource) {
        this.resourceId = resource.getId();
        this.itemId = resource.getItem().getId();
        update(resource);
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getItemId() {
        return itemId;
    }

    public void update(Resource resource) {
        imported = resource.isImported();
        exported = resource.isExported();
    }

    public boolean isImported() {
        return imported;
    }

    public boolean isExported() {
        return exported;
    }

    public Set<Production> getProducers() {
        return producers;
    }

    public QuantityByChangelist getProduced() {
        // we only use cached values anyway
        return producers.stream().map(producer -> producer.getOutput(itemId)).filter(Objects::nonNull)
                .reduce(QuantityByChangelist.ZERO, QuantityByChangelist::add);
    }

    public QuantityByChangelist getOverProduced() {
        return getProduced().subtract(getConsumed());
    }

    public Set<Production> getConsumers() {
        return consumers;
    }

    public QuantityByChangelist getConsumed() {
        // we only use cached values anyway
        return consumers.stream().map(consumer -> consumer.getInput(itemId)).filter(Objects::nonNull)
                .reduce(QuantityByChangelist.ZERO, QuantityByChangelist::add);
    }

    public QuantityByChangelist getOverConsumed() {
        return getConsumed().subtract(getProduced());
    }

    @Override
    public String toString() {
        return "[current = " + getProduced().getCurrent() + " - " + getConsumed().getCurrent() + ", primary = "
                + getProduced().getWithPrimaryChangelist() + " - " + getConsumed().getWithPrimaryChangelist()
                + ", active = " + getProduced().getWithActiveChangelists() + " - "
                + getConsumed().getWithActiveChangelists() + "]";
    }

}
