package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.QuantityByChangelist;
import de.yggdrasil128.factorial.model.resource.Resource;
import de.yggdrasil128.factorial.model.resource.local.LocalResource;

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
    private boolean importExport;
    private final Set<Production> producers = new HashSet<>();
    private final Set<Production> consumers = new HashSet<>();

    public ResourceContributions(Resource resource) {
        this.resourceId = resource.getId();
        this.itemId = resource.getItem().getId();
        if (resource instanceof LocalResource casted) {
            update(casted);
        }
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getItemId() {
        return itemId;
    }

    public boolean update(LocalResource resource) {
        boolean result = importExport != resource.isImportExport();
        importExport = resource.isImportExport();
        return result;
    }

    public boolean isImportExport() {
        return importExport;
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
