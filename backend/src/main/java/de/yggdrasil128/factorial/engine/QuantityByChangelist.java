package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;

public class QuantityByChangelist {

    private final Fraction current;
    private final Fraction withPrimaryChangelist;
    private final Fraction withActiveChangelists;

    public static QuantityByChangelist allAt(Fraction initial) {
        return new QuantityByChangelist(initial, initial, initial);
    }

    public QuantityByChangelist(Fraction current, Fraction withPrimaryChangelist, Fraction withActiveChangelists) {
        this.current = current;
        this.withPrimaryChangelist = withPrimaryChangelist;
        this.withActiveChangelists = withActiveChangelists;
    }

    public Fraction getCurrent() {
        return current;
    }

    public Fraction getWithPrimaryChangelist() {
        return withPrimaryChangelist;
    }

    public Fraction getWithActiveChangelists() {
        return withActiveChangelists;
    }

    public QuantityByChangelist add(QuantityByChangelist that) {
        return new QuantityByChangelist(this.current.add(that.current),
                this.withPrimaryChangelist.add(that.withPrimaryChangelist),
                this.withActiveChangelists.add(that.withActiveChangelists));
    }

    public QuantityByChangelist subtract(QuantityByChangelist that) {
        return new QuantityByChangelist(this.current.subtract(that.current),
                this.withPrimaryChangelist.subtract(that.withPrimaryChangelist),
                this.withActiveChangelists.subtract(that.withActiveChangelists));
    }

    @Override
    public String toString() {
        return "[ current = " + current + ", primary = " + withActiveChangelists + ", active = " + withActiveChangelists
                + " ]";
    }

}
