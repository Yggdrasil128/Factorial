package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.item.Item;

import java.util.Map;

/**
 * Describes an entity that maintains a specific set of inputs and outputs.
 */
public interface Production {

    /**
     * Returns the production inputs.
     * <p>
     * The keys of this mapping are values of {@link Item#getId() Item.id}.
     * 
     * @return the production inputs
     */
    Map<Integer, QuantityByChangelist> getInputs();

    /**
     * Returns the production input for the target item.
     * <p>
     * This method is provided as default method so that classes can override it with a more efficient implementation.
     * 
     * @param itemId the {@link Item#getId() id} of the target item
     * @return the production input for the target item
     */
    default QuantityByChangelist getInput(int itemId) {
        return getInputs().get(itemId);
    }

    /**
     * Returns the production outputs.
     * <p>
     * The keys of this mapping are values of {@link Item#getId() Item.id}.
     * 
     * @return the production outputs
     */
    Map<Integer, QuantityByChangelist> getOutputs();

    /**
     * Returns the production output for the target item.
     * <p>
     * This method is provided as default method so that classes can override it with a more efficient implementation.
     * 
     * @param itemId the {@link Item#getId() id} of the target item
     * @return the production output for the target item
     */
    default QuantityByChangelist getOutput(int itemId) {
        return getOutputs().get(itemId);
    }

}
