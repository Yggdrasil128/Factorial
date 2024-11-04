package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.changelist.Changelist;

import java.util.Objects;
import java.util.function.BinaryOperator;

/**
 * Bundles three {@link Fraction} values according to primary/active {@link Changelist Changelists}.
 * <p>
 * Instances of this class are immutable and value-based.
 */
public class QuantityByChangelist {

    private final Fraction current;
    private final Fraction withPrimaryChangelist;
    private final Fraction withActiveChangelists;

    public static QuantityByChangelist ZERO = QuantityByChangelist.allAt(Fraction.ZERO);

    public static QuantityByChangelist allAt(Fraction initial) {
        return new QuantityByChangelist(initial, initial, initial);
    }

    public QuantityByChangelist(Fraction current, Fraction withPrimaryChangelist, Fraction withActiveChangelists) {
        this.current = current;
        this.withPrimaryChangelist = withPrimaryChangelist;
        this.withActiveChangelists = withActiveChangelists;
    }

    public boolean isZero() {
        return current.isZero() && withPrimaryChangelist.isZero() && withActiveChangelists.isZero();
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
        return compute(that, Fraction::add);
    }

    public QuantityByChangelist add(Fraction value) {
        return new QuantityByChangelist(current.add(value), withPrimaryChangelist.add(value),
                withActiveChangelists.add(value));
    }

    public QuantityByChangelist subtract(QuantityByChangelist consumed) {
        return compute(consumed, Fraction::subtract);
    }

    private QuantityByChangelist compute(QuantityByChangelist that, BinaryOperator<Fraction> arithmeticOperation) {
        return new QuantityByChangelist(arithmeticOperation.apply(this.current, that.current),
                arithmeticOperation.apply(this.withPrimaryChangelist, that.withPrimaryChangelist),
                arithmeticOperation.apply(this.withActiveChangelists, that.withActiveChangelists));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QuantityByChangelist)) {
            return false;
        }
        QuantityByChangelist that = (QuantityByChangelist) obj;
        return this.current.equals(that.current) && this.withPrimaryChangelist.equals(that.withPrimaryChangelist)
                && this.withActiveChangelists.equals(that.withActiveChangelists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(current, withPrimaryChangelist, withActiveChangelists);
    }

    @Override
    public String toString() {
        return "[current = " + current + ", primary = " + withPrimaryChangelist + ", active = " + withActiveChangelists
                + " ]";
    }

}
