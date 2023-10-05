package de.yggdrasil128.factorial.model;

public class Balance {

    private Fraction production = Fraction.ZERO;
    private Fraction consumption = Fraction.ZERO;

    public Fraction getProduction() {
        return production;
    }

    public void setProduction(Fraction production) {
        this.production = production;
    }

    public Fraction getConsumption() {
        return consumption;
    }

    public void setConsumption(Fraction consumption) {
        this.consumption = consumption;
    }

    public Fraction getNet() {
        return production.subtract(consumption);
    }

    @Override
    public String toString() {
        return production + " / " + consumption;
    }

}
