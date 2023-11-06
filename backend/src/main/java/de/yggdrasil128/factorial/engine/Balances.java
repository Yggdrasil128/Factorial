package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;

public class Balances {

    private QuantityByChangelist productionCapacity = QuantityByChangelist.allAt(Fraction.ZERO);
    private QuantityByChangelist productionRequired = QuantityByChangelist.allAt(Fraction.ZERO);
    private QuantityByChangelist consumptionCapacity = QuantityByChangelist.allAt(Fraction.ZERO);
    private QuantityByChangelist consumptionRequired = QuantityByChangelist.allAt(Fraction.ZERO);
    private boolean transportedIn;
    private boolean transportedOut;

    public QuantityByChangelist getProductionCapacity() {
        return productionCapacity;
    }

    public QuantityByChangelist getProductionRequired() {
        return productionRequired;
    }

    public void recordProduction(QuantityByChangelist production, boolean required) {
        productionCapacity = productionCapacity.add(production);
        if (required) {
            productionRequired = productionRequired.add(production);
        }
    }

    public QuantityByChangelist getConsumptionCapacity() {
        return consumptionCapacity;
    }

    public QuantityByChangelist getConsumptionRequired() {
        return consumptionRequired;
    }

    public void recordConsumption(QuantityByChangelist consumption, boolean required) {
        consumptionCapacity = consumptionCapacity.add(consumption);
        if (required) {
            consumptionRequired = consumptionRequired.add(consumption);
        }
    }

    public QuantityByChangelist getProductionAvailable() {
        return productionCapacity.subtract(consumptionRequired);
    }

    public QuantityByChangelist getConsumptionAvailable() {
        return consumptionCapacity.subtract(productionRequired);
    }

    public boolean isTransportedIn() {
        return transportedIn;
    }

    public void setTransportedIn(boolean transportedIn) {
        this.transportedIn = transportedIn;
    }

    public boolean isTransportedOut() {
        return transportedOut;
    }

    public void setTransportedOut(boolean transportedOut) {
        this.transportedOut = transportedOut;
    }

}
