package de.yggdrasil128.factorial.engine;

import java.util.Map;

public interface Production {

    Map<Integer, QuantityByChangelist> getInputs();

    Map<Integer, QuantityByChangelist> getOutputs();

}
