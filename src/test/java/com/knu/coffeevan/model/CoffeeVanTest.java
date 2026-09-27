package com.knu.coffeevan.model;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CoffeeVanTest {

    @ParameterizedTest
    @CsvSource({
        "0, 1000",
        "-1, 1000",
        "1000, 0",
        "1000, -1"
    })
    void shouldRejectInvalidVanLimits(long capacity, long budget) {
        assertThrows(IllegalArgumentException.class,
                () -> new CoffeeVan(capacity, budget));
    }

    @Test
    void shouldInitializeEmptyVan() {
        CoffeeVan van = new CoffeeVan(1_000, 10_000);

        assertEquals(1_000, van.getCapacity());
        assertEquals(10_000, van.getBudget());
        assertEquals(0, van.getUsedVolume());
        assertEquals(0, van.getTotalPrice());
        assertTrue(van.getCargo().isEmpty());
    }

    @Test
    void shouldLoadUpToExactLimitsAcrossMultipleBatches() {
        CoffeeVan van = new CoffeeVan(500, 3_000);
        Coffee first = coffee("First", 100, 200, 1_000, 80);
        Coffee second = coffee("Second", 150, 300, 2_000, 90);

        van.loadAll(List.of(first));
        van.loadAll(List.of(second));

        assertEquals(List.of(first, second), van.getCargo());
        assertEquals(500, van.getUsedVolume());
        assertEquals(3_000, van.getTotalPrice());
    }

    @ParameterizedTest
    @CsvSource({
        "499, 3000",
        "500, 2999"
    })
    void shouldRejectWholeBatchWhenAnyLimitIsExceeded(
            long capacity, long budget) {
        CoffeeVan van = new CoffeeVan(capacity, budget);
        Coffee existing = coffee("Existing", 50, 100, 500, 75);
        Coffee first = coffee("First", 100, 200, 1_000, 80);
        Coffee second = coffee("Second", 100, 200, 1_500, 90);

        van.loadAll(List.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> van.loadAll(List.of(first, second)));

        assertEquals(List.of(existing), van.getCargo());
        assertEquals(100, van.getUsedVolume());
        assertEquals(500, van.getTotalPrice());
    }

    @Test
    void shouldCountExternalPackageVolumeRatherThanNetWeight() {
        CoffeeVan van = new CoffeeVan(500, 10_000);
        Coffee jar = new InstantCoffee(
                "Large jar", 100, 600, 1_000, 80,
                InstantCoffee.Packaging.JAR);

        assertThrows(IllegalArgumentException.class,
                () -> van.loadAll(List.of(jar)));

        assertTrue(van.getCargo().isEmpty());
    }

    @Test
    void shouldRejectNullCollectionAndNullElements() {
        CoffeeVan van = new CoffeeVan(1_000, 10_000);
        Coffee coffee = coffee("Coffee", 100, 200, 1_000, 80);

        assertThrows(NullPointerException.class,
                () -> van.loadAll(null));
        assertThrows(NullPointerException.class,
                () -> van.loadAll(Arrays.asList(coffee, null)));

        assertTrue(van.getCargo().isEmpty());
        assertEquals(0, van.getUsedVolume());
        assertEquals(0, van.getTotalPrice());
    }

    @Test
    void shouldAllowEmptyBatch() {
        CoffeeVan van = new CoffeeVan(1_000, 10_000);

        van.loadAll(List.of());

        assertTrue(van.getCargo().isEmpty());
        assertEquals(0, van.getUsedVolume());
        assertEquals(0, van.getTotalPrice());
    }

    @Test
    void shouldSortByRatioRatherThanAbsolutePrice() {
        CoffeeVan van = new CoffeeVan(10_000, 100_000);
        Coffee expensiveRatio = coffee("Small", 100, 200, 3_000, 80);
        Coffee cheapRatio = coffee("Large", 1_000, 2_000, 10_000, 90);
        Coffee middleRatio = coffee("Medium", 500, 1_000, 10_000, 85);

        van.loadAll(List.of(expensiveRatio, cheapRatio, middleRatio));
        van.sortByPriceToWeight();

        assertEquals(
                List.of(cheapRatio, middleRatio, expensiveRatio),
                van.getCargo());
    }

    @Test
    void shouldPreserveLoadingOrderForEqualRatios() {
        CoffeeVan van = new CoffeeVan(1_000, 10_000);
        Coffee first = coffee("First", 100, 200, 1_000, 80);
        Coffee second = coffee("Second", 200, 300, 2_000, 90);

        van.loadAll(List.of(first, second));
        van.sortByPriceToWeight();

        assertEquals(List.of(first, second), van.getCargo());
    }

    @Test
    void shouldCompareLargeValuesWithoutIntegerOverflow() {
        CoffeeVan van = new CoffeeVan(Long.MAX_VALUE, Long.MAX_VALUE);
        Coffee higherRatio = coffee(
                "Higher", Integer.MAX_VALUE - 1, 1,
                Integer.MAX_VALUE, 80);
        Coffee lowerRatio = coffee(
                "Lower", Integer.MAX_VALUE, 1,
                Integer.MAX_VALUE - 1, 80);

        van.loadAll(List.of(higherRatio, lowerRatio));
        van.sortByPriceToWeight();

        assertEquals(List.of(lowerRatio, higherRatio), van.getCargo());
        assertEquals(4_294_967_293L, van.getTotalPrice());
    }

    @Test
    void shouldSearchUsingInclusiveQualityBoundaries() {
        CoffeeVan van = new CoffeeVan(10_000, 100_000);
        Coffee below = coffee("Below", 100, 200, 1_000, 79);
        Coffee minimum = coffee("Minimum", 100, 200, 1_000, 80);
        Coffee middle = coffee("Middle", 100, 200, 1_000, 85);
        Coffee maximum = coffee("Maximum", 100, 200, 1_000, 90);
        Coffee above = coffee("Above", 100, 200, 1_000, 91);

        van.loadAll(List.of(below, minimum, middle, maximum, above));

        assertEquals(List.of(minimum, middle, maximum),
                van.findByQuality(80, 90));
        assertEquals(List.of(middle), van.findByQuality(85, 85));
        assertEquals(van.getCargo(), van.findByQuality(0, 100));
        assertTrue(van.findByQuality(95, 100).isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
        "-1, 80",
        "80, 101",
        "90, 80"
    })
    void shouldRejectInvalidQualityRanges(int minimum, int maximum) {
        CoffeeVan van = new CoffeeVan(1_000, 10_000);

        assertThrows(IllegalArgumentException.class,
                () -> van.findByQuality(minimum, maximum));
    }

    @Test
    void shouldHandleSortingAndSearchingEmptyVan() {
        CoffeeVan van = new CoffeeVan(1_000, 10_000);

        van.sortByPriceToWeight();

        assertTrue(van.getCargo().isEmpty());
        assertTrue(van.findByQuality(0, 100).isEmpty());
    }

    @Test
    void shouldReturnImmutableCargoSnapshotAndSearchResults() {
        CoffeeVan van = new CoffeeVan(1_000, 10_000);
        Coffee first = coffee("First", 100, 200, 1_000, 80);
        Coffee second = coffee("Second", 100, 200, 1_000, 90);

        van.loadAll(List.of(first));
        List<Coffee> snapshot = van.getCargo();
        List<Coffee> matches = van.findByQuality(0, 100);

        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.add(second));
        assertThrows(UnsupportedOperationException.class,
                matches::clear);

        van.loadAll(List.of(second));

        assertEquals(List.of(first), snapshot);
        assertEquals(List.of(first), matches);
        assertEquals(List.of(first, second), van.getCargo());
    }

    private Coffee coffee(String name, int weight, int volume,
            int price, int quality) {
        return new BeanCoffee(name, weight, volume, price, quality);
    }
}
