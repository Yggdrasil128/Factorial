package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;

public class Balance {

    private QuantityByChangelist total = QuantityByChangelist.allAt(Fraction.ZERO);
    private QuantityByChangelist required = QuantityByChangelist.allAt(Fraction.ZERO);

    /**
     * The total amount of production / consumption.
     * @return the total amount of production / consumption
     */
    public QuantityByChangelist getTotal() {
        return total;
    }

    /**
     * The "required" amount of production / consumption.
     * <p>
     * Therefore either:
     * <ul>
     * <li>the minimum amount of production that is required to avoid starvation of inputs</li>
     * <li>the minimum amount of consumption that is required to avoid congestion of outputs</li>
     * </ul>
     * 
     * @return the "used" amount of production / consumption
     */
    public QuantityByChangelist getRequired() {
        return required;
    }

    /**
     * The amount of production / consumption that is available for reallocation, hence without anything else breaking.
     * @return amount of production / consumption that is available for reallocation
     */
    public QuantityByChangelist getAvailable() {
        return total.subtract(required);
    }

    void addTotal(QuantityByChangelist quantities) {
        total = total.add(quantities);
    }

    void addRequired(QuantityByChangelist quantities) {
        required = required.add(quantities);
    }

}
