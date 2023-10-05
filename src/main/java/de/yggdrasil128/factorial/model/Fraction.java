package de.yggdrasil128.factorial.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = FractionDeserializer.class)
public class Fraction extends Number {

    // generated
    private static final long serialVersionUID = 8312326480060238103L;

    public static final Fraction ZERO = Fraction.of(0);
    public static final Fraction ONE = Fraction.of(1);

    private long numerator;
    private long denominator;

    public static Fraction of(long value) {
        return new Fraction(value);
    }

    public static Fraction of(long n, long d) {
        return new Fraction(n, d);
    }

    public static Fraction of(String s) {
        int index = s.indexOf('/');
        if (index == -1) {
            return new Fraction(Long.parseLong(s));
        }
        return new Fraction(
                Long.parseLong(s.substring(0, index).trim()),
                Long.parseLong(s.substring(index + 1).trim())
        );
    }

    private Fraction(long value) {
        numerator = value;
        denominator = 1;
    }

    private Fraction(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        normalize();
    }

    private static long gcd(long a, long b) {
        // Euclid's algorithm
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    private void normalize() {
        if (denominator == 0) {
            throw new ArithmeticException("Division by zero");
        }
        if (denominator < 0) {
            numerator *= -1;
            denominator *= -1;
        }
        long gcd = Fraction.gcd(numerator, denominator);
        if (gcd == 1) {
            return;
        }
        numerator /= gcd;
        denominator /= gcd;
    }

    public Fraction add(Fraction that) {
        return new Fraction(
                this.numerator * that.denominator + that.numerator * this.denominator,
                this.denominator * that.denominator
        );
    }

    public Fraction negative() {
        return new Fraction(-this.numerator, this.denominator);
    }

    public Fraction subtract(Fraction that) {
        return add(that.negative());
    }

    public Fraction multiply(Fraction that) {
        return new Fraction(
                this.numerator * that.numerator,
                this.denominator * that.denominator
        );
    }

    public Fraction inverse() {
        return new Fraction(this.denominator, this.numerator);
    }

    public Fraction divide(Fraction that) {
        return multiply(that.inverse());
    }

    @Override
    public int intValue() {
        return (int) doubleValue();
    }

    @Override
    public long longValue() {
        return (long) doubleValue();
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        return (double) numerator / (double) denominator;
    }

    @Override
    public String toString() {
        if (denominator == 1) {
            return String.valueOf(numerator);
        }
        return numerator + "/" + denominator;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        Fraction fraction = (Fraction) that;

        if (numerator != fraction.numerator) return false;
        return denominator == fraction.denominator;
    }

    @Override
    public int hashCode() {
        int result = (int) (numerator ^ (numerator >>> 32));
        result = 31 * result + (int) (denominator ^ (denominator >>> 32));
        return result;
    }

}
