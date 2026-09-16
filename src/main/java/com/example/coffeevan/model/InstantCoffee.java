package com.example.coffeevan.model;

import java.util.Objects;

public final class InstantCoffee extends Coffee{
    public enum Packaging {
        JAR,
        SACHET
    }

    private final Packaging packaging;

    public InstantCoffee(String name, int weightGrams, int volumeMilliliters, int price, int quality, Packaging packaging){
        super(name, weightGrams, volumeMilliliters, price, quality);
        
        this.packaging = Objects.requireNonNull(packaging, "Packaging must be not null");
    }

    @Override
    public String getForm(){
        return "Instant";
    }

    @Override
    public String getPackaging(){
        return packaging.name();
    }
}