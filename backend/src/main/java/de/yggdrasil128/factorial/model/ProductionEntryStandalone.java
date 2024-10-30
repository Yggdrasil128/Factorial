package de.yggdrasil128.factorial.model;

import de.yggdrasil128.factorial.engine.QuantityByChangelist;

import java.util.Map;

public record ProductionEntryStandalone(int itemId, QuantityByChangelist quantity) {

    public static ProductionEntryStandalone of(Map.Entry<Integer, QuantityByChangelist> entry) {
        return new ProductionEntryStandalone(entry.getKey().intValue(), entry.getValue());
    }

}
