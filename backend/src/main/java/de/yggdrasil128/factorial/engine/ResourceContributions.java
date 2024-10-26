package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.resource.Resource;

import java.util.HashSet;
import java.util.Set;

/**
 * For a single item, represents how much of it is produced and consumed within a <i>production line</i>.
 * 
 * @see ProductionLineResources
 */
public class ResourceContributions {

    private static QuantityByChangelist ZERO = QuantityByChangelist.allAt(Fraction.ZERO);

    // we must not keep references to the respective entities here
    private final int resourceId;
    private final int itemId;
    private final Set<Production> producers = new HashSet<>();
    private final Set<Production> consumers = new HashSet<>();

    public ResourceContributions(Resource resource) {
        this.resourceId = resource.getId();
        this.itemId = resource.getItem().getId();
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getItemId() {
        return itemId;
    }

    public Set<Production> getProducers() {
        return producers;
    }

    public QuantityByChangelist getProduced() {
        // we only use cached values anyway
        return producers.stream().map(producer -> producer.getOutputs().get(itemId)).reduce(ZERO,
                QuantityByChangelist::add);
    }

    public Set<Production> getConsumers() {
        return consumers;
    }

    public QuantityByChangelist getConsumed() {
        // we only use cached values anyway
        return consumers.stream().map(consumer -> consumer.getInputs().get(itemId)).reduce(ZERO,
                QuantityByChangelist::add);
    }

    @Override
    public String toString() {
        return "[current = " + getProduced().getCurrent() + " - " + getConsumed().getCurrent() + ", primary = "
                + getProduced().getWithPrimaryChangelist() + " - " + getConsumed().getWithPrimaryChangelist()
                + ", active = " + getProduced().getWithActiveChangelists() + " - "
                + getConsumed().getWithActiveChangelists() + "]";
    }

}
