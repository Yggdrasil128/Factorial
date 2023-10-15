package de.yggdrasil128.factorial.api.factoryitemlist;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.QuantityByChangelist;

public class Balances {

    private QuantityByChangelist production = QuantityByChangelist.at(Fraction.ZERO);
    private QuantityByChangelist consumption = QuantityByChangelist.at(Fraction.ZERO);

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
