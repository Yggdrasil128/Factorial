package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;

public class Balances {

    private QuantityByChangelist production = QuantityByChangelist.allAt(Fraction.ZERO);
    private QuantityByChangelist consumption = QuantityByChangelist.allAt(Fraction.ZERO);

    public QuantityByChangelist getProduction() {
        return production;
    }

    public void setProduction(QuantityByChangelist production) {
        this.production = production;
    }

    public QuantityByChangelist getConsumption() {
        return consumption;
    }

    public void setConsumption(QuantityByChangelist consumption) {
        this.consumption = consumption;
    }

    public QuantityByChangelist getNet() {
        return production.subtract(consumption);
    }

}
