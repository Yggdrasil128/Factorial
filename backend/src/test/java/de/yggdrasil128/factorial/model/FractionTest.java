package de.yggdrasil128.factorial.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FractionTest {
    @Test
    public void testBasicArithmetic() {
        assertEquals(Fraction.of(-3), Fraction.of(6, -2));


        assertEquals(Fraction.of(29, 35),
                Fraction.of(3, 7).add(Fraction.of(2, 5)));

        assertEquals(Fraction.of(6, 35),
                Fraction.of(3, 7).multiply(Fraction.of(2, 5)));


        assertEquals(Fraction.of(13, 30),
                Fraction.of(3, 10).add(Fraction.of(2, 15)));

        assertEquals(Fraction.of(1, 25),
                Fraction.of(3, 10).multiply(Fraction.of(2, 15)));

        assertThrows(ArithmeticException.class, () -> Fraction.of(0).inverse());

        assertEquals(Fraction.of(1, 10),
                Fraction.of(1, 5).subtract(Fraction.of(1, 10)));

        assertEquals(Fraction.of(3, 4),
                Fraction.of(3, 2).divide(Fraction.of(2)));
    }

    @Test
    public void testParseAndToString() {
        assertEquals(Fraction.of("4"), Fraction.of(4));
        assertEquals(Fraction.of("4/3"), Fraction.of(4, 3));
        assertEquals(Fraction.of("8 / 2"), Fraction.of(4, 1));

        assertEquals(Fraction.of(4).toString(), "4");
        assertEquals(Fraction.of(4, 3).toString(), "4/3");
    }

}