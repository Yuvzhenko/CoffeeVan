package com.knu.coffeevan.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CoffeeTest {

    @Test
    void shouldExposeCommonProperties() {
        Coffee coffee = new BeanCoffee("Arabica", 500, 900, 25_000, 90);

        assertEquals("Arabica", coffee.getName());
        assertEquals(500, coffee.getWeight());
        assertEquals(900, coffee.getVolume());
        assertEquals(25_000, coffee.getPrice());
        assertEquals(90, coffee.getQuality());
    }

    @Test
    void shouldProvidePolymorphicFormsAndPackaging() {
        Coffee beans = new BeanCoffee("Beans", 100, 200, 5_000, 90);
        Coffee ground = new GroundCoffee("Ground", 100, 180, 4_000, 85);
        Coffee jar = new InstantCoffee("Jar", 100, 300, 6_000, 75,
                InstantCoffee.Packaging.JAR);
        Coffee sachet = new InstantCoffee("Sachet", 10, 30, 500, 70,
                InstantCoffee.Packaging.SACHET);

        assertEquals("Beans", beans.getForm());
        assertEquals("Bag", beans.getPackaging());
        assertEquals("Ground", ground.getForm());
        assertEquals("Bag", ground.getPackaging());
        assertEquals("Instant", jar.getForm());
        assertEquals("JAR", jar.getPackaging());
        assertEquals("Instant", sachet.getForm());
        assertEquals("SACHET", sachet.getPackaging());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void shouldRejectInvalidNames(String name) {
        Class<? extends Throwable> expected = name == null
                ? NullPointerException.class
                : IllegalArgumentException.class;

        assertThrows(expected,
                () -> new BeanCoffee(name, 100, 200, 1_000, 80));
    }

    @ParameterizedTest
    @CsvSource({
        "0, 200, 1000, 80",
        "-1, 200, 1000, 80",
        "100, 0, 1000, 80",
        "100, -1, 1000, 80",
        "100, 200, 0, 80",
        "100, 200, -1, 80",
        "100, 200, 1000, -1",
        "100, 200, 1000, 101"
    })
    void shouldRejectInvalidNumericProperties(
            int weight, int volume, int price, int quality) {
        assertThrows(IllegalArgumentException.class,
                () -> new BeanCoffee(
                        "Coffee", weight, volume, price, quality));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100})
    void shouldAcceptQualityBoundaries(int quality) {
        Coffee coffee = new GroundCoffee(
                "Coffee", 100, 200, 1_000, quality);

        assertEquals(quality, coffee.getQuality());
    }

    @Test
    void shouldRejectNullInstantPackaging() {
        assertThrows(NullPointerException.class,
                () -> new InstantCoffee(
                        "Coffee", 100, 200, 1_000, 80, null));
    }
}
