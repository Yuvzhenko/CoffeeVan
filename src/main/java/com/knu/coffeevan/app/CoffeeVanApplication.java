package com.knu.coffeevan.app;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import com.knu.coffeevan.model.BeanCoffee;
import com.knu.coffeevan.model.Coffee;
import com.knu.coffeevan.model.CoffeeVan;
import com.knu.coffeevan.model.GroundCoffee;
import com.knu.coffeevan.model.InstantCoffee;

public final class CoffeeVanApplication{
    private final PrintStream output;

    public CoffeeVanApplication(PrintStream output){
        this.output = Objects.requireNonNull(output, "Output musn't be null");
    }

    public static void main(String[] args){
        new CoffeeVanApplication(System.out).run();
    }

     public void run() {
        CoffeeVan van = new CoffeeVan(5_000, 150_000);

        van.loadAll(List.of(
                new BeanCoffee(
                        "Arabica Colombia", 1_000, 2_000, 60_000, 92),
                new GroundCoffee(
                        "Arabica Ethiopia", 500, 900, 28_000, 88),
                new InstantCoffee(
                        "Classic", 200, 600, 18_000, 75,
                        InstantCoffee.Packaging.JAR),
                new InstantCoffee(
                        "Travel Blend", 100, 250, 8_000, 82,
                        InstantCoffee.Packaging.SACHET)
        ));

        output.println("Loaded units: " + van.getCargo().size());
        output.println("Volume: " + van.getUsedVolume()
                + "/" + van.getCapacity() + " ml");
        output.println("Cost: " + formatMoney(van.getTotalPrice())
                + "/" + formatMoney(van.getBudget()) + " UAH");

        van.sortByPriceToWeight();

        output.println("Sorted by price/weight:");
        van.getCargo().forEach(this::printCoffee);

        output.println("Quality range [80, 95]:");
        van.findByQuality(80, 95).forEach(this::printCoffee);
    }

    private void printCoffee(Coffee coffee){
        BigDecimal pricePerKilo = BigDecimal.valueOf(coffee.getPrice())
                                        .multiply(BigDecimal.TEN)
                                        .divide(
                                            BigDecimal.valueOf(coffee.getWeight()),
                                            2, RoundingMode.HALF_UP);
        output.println(coffee.getName()
                        + " | " + coffee.getForm()
                        + " | " + coffee.getPackaging()
                        + " | quality = " + coffee.getQuality()
                        + " | " + pricePerKilo.toPlainString() + " UAN/kg");
    }

    private String formatMoney(long kopecks){
        return BigDecimal.valueOf(kopecks, 2).toPlainString();
    }

}