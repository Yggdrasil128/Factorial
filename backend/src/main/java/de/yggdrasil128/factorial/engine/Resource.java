package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.item.Item;

import java.util.ArrayList;
import java.util.List;

// not yet an @Entity, but coming soon
public class Resource {

    private static QuantityByChangelist ZERO = QuantityByChangelist.allAt(Fraction.ZERO);

    private final Item item;
    private final boolean imported = false;
    private final boolean exported = false;
    private final List<Production> producers = new ArrayList<>();
    private final List<Production> consumers = new ArrayList<>();

    public Resource(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public boolean isImported() {
        return imported;
    }

    public boolean isExported() {
        return exported;
    }

    public List<Production> getProducers() {
        return producers;
    }

    public QuantityByChangelist getProduced() {
        return producers.stream().map(producer -> producer.getOutputs().getOrDefault(item, ZERO)).reduce(ZERO,
                QuantityByChangelist::add);
    }

    public List<Production> getConsumers() {
        return consumers;
    }

    public QuantityByChangelist getConsumed() {
        return consumers.stream().map(consumer -> consumer.getInputs().getOrDefault(item, ZERO)).reduce(ZERO,
                QuantityByChangelist::add);
    }

}
