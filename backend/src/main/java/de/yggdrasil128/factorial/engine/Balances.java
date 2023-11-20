package de.yggdrasil128.factorial.engine;

public class Balances {

    private final Balance production = new Balance();
    private final Balance consumption = new Balance();
    private boolean transportedIn;
    private boolean transportedOut;

    public Balance getProduction() {
        return production;
    }

    public Balance getConsumption() {
        return consumption;
    }

    void recordProduction(QuantityByChangelist quantities, boolean required) {
        this.production.addTotal(quantities);
        if (required) {
            this.consumption.addRequired(quantities);
        }
    }

    void recordConsumption(QuantityByChangelist quantities, boolean required) {
        this.consumption.addTotal(quantities);
        if (required) {
            this.production.addRequired(quantities);
        }
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
