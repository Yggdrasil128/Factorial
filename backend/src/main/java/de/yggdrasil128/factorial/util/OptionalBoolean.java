package de.yggdrasil128.factorial.util;

import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * @see OptionalInt
 */
public enum OptionalBoolean {

    EMPTY, TRUE, FALSE;

    public static OptionalBoolean empty() {
        return EMPTY;
    }

    public static OptionalBoolean of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static OptionalBoolean ofNullable(Boolean value) {
        return null == value ? EMPTY : of(value.booleanValue());
    }

    public boolean getAsBoolean() {
        if (isPresent()) {
            return getAsBoolean0();
        }
        throw new NoSuchElementException("No value present");
    }

    private boolean getAsBoolean0() {
        return TRUE == this;
    }

    public boolean isPresent() {
        return EMPTY != this;
    }

    public boolean orElse(boolean other) {
        return isPresent() ? getAsBoolean0() : other;
    }

    public boolean orElseGet(BooleanSupplier other) {
        return isPresent() ? getAsBoolean0() : other.getAsBoolean();
    }

    public <X extends Throwable> boolean orElseThrow(Supplier<X> exceptionSupplier) throws X {
        if (isPresent()) {
            return getAsBoolean0();
        }
        throw exceptionSupplier.get();
    }

    @Override
    public String toString() {
        return isPresent() ? ("OptionalBoolean[" + getAsBoolean0() + "'") : "OptionalBoolean.empty";
    }

}
