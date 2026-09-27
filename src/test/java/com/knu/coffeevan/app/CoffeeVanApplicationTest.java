package com.knu.coffeevan.app;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class CoffeeVanApplicationTest {

    @Test
    void shouldRejectNullOutput() {
        assertThrows(NullPointerException.class,
                () -> new CoffeeVanApplication(null));
    }

    @Test
    void shouldPrintSummarySortedCargoAndQualityMatches() {
        PrintStream output = mock(PrintStream.class);
        CoffeeVanApplication application =
                new CoffeeVanApplication(output);

        application.run();

        String ground = "Arabica Ethiopia | Ground | Bag | quality = 88 | 560.00 UAH/kg";
        String beans = "Arabica Colombia | Beans | Bag | quality = 92 | 600.00 UAH/kg";
        String sachet = "Travel Blend | Instant | SACHET | quality = 82 | 800.00 UAH/kg";
        String jar = "Classic | Instant | JAR | quality = 75 | 900.00 UAH/kg";

        InOrder inOrder = inOrder(output);

        inOrder.verify(output).println("Loaded units: 4");
        inOrder.verify(output).println("Volume: 3750/5000 ml");
        inOrder.verify(output).println("Cost: 1140.00/1500.00 UAH");

        inOrder.verify(output).println("Sorted by price/weight:");
        inOrder.verify(output).println(ground);
        inOrder.verify(output).println(beans);
        inOrder.verify(output).println(sachet);
        inOrder.verify(output).println(jar);

        inOrder.verify(output).println("Quality range [80, 95]:");
        inOrder.verify(output).println(ground);
        inOrder.verify(output).println(beans);
        inOrder.verify(output).println(sachet);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldPrintToRealStreamCorrectly() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos, true, StandardCharsets.UTF_8);
        
        CoffeeVanApplication application = new CoffeeVanApplication(printStream);
        application.run();
        
        String actualOutput = baos.toString(StandardCharsets.UTF_8);

        assertTrue(actualOutput.contains("Loaded units: 4"));
        assertTrue(actualOutput.contains("Sorted by price/weight:"));
        assertTrue(actualOutput.contains("Quality range [80, 95]:"));
        assertTrue(actualOutput.contains("Arabica Colombia | Beans | Bag | quality = 92 | 600.00 UAH/kg"));
    }
}