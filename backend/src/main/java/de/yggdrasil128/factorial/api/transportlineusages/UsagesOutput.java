package de.yggdrasil128.factorial.api.transportlineusages;

import de.yggdrasil128.factorial.engine.TransportLineUsage;
import de.yggdrasil128.factorial.model.factory.Factory;
import de.yggdrasil128.factorial.model.item.Item;

import java.util.List;
import java.util.Map;

public class UsagesOutput {

    private final List<UsageItemOutput> items;

    public UsagesOutput(Map<Item, Map<Factory, TransportLineUsage>> delegate) {
        items = delegate.entrySet().stream().map(entry -> new UsageItemOutput(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<UsageItemOutput> getItems() {
        return items;
    }

}
