package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.item.Item;

import java.util.Map;

public interface Production {

    Map<Item, QuantityByChangelist> getInputs();

    Map<Item, QuantityByChangelist> getOutputs();

}
