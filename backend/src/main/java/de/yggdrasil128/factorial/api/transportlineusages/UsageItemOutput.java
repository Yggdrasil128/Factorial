package de.yggdrasil128.factorial.api.transportlineusages;

import de.yggdrasil128.factorial.engine.TransportLineUsage;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;
import de.yggdrasil128.factorial.model.item.ItemOutput;

import java.util.List;
import java.util.Map;

public class UsageItemOutput extends ItemOutput {

    private final List<UsageFactoryOutput> factories;

    public UsageItemOutput(Item delegate, Map<Factory, TransportLineUsage> usages) {
        super(delegate);
        factories = usages.entrySet().stream().map(entry -> new UsageFactoryOutput(entry.getKey(),
                entry.getValue())).toList();
    }

    public List<UsageFactoryOutput> getFactories() {
        return factories;
    }

}
