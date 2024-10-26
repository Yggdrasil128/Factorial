package de.yggdrasil128.factorial.engine;

import de.yggdrasil128.factorial.model.Fraction;
import de.yggdrasil128.factorial.model.changelist.Changelist;

import java.util.Objects;

/**
 * Bundles three {@link Fraction} values according to primary/active {@link Changelist Changelists}.
 * <p>
 * Instances of this class are immutable and value-based.
 */
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

    public QuantityByChangelist add(Fraction value) {
        return new QuantityByChangelist(current.add(value), withPrimaryChangelist.add(value),
                withActiveChangelists.add(value));
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
