package com.knu.coffeevan.model;

import java.util.Objects;

public abstract class Coffee{

    private final String name;
    private final int weightGrams;
    private final int volumeMilliliters;
    private final int price;
    private final int quality;

    protected Coffee(String name, int weightGrams, int volumeMilliliters, int price, int quality){
        Objects.requireNonNull(name, "Name must not be null");

        if (name.isBlank()){
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (weightGrams <= 0){
            throw  new IllegalArgumentException("Weight must be more then zero");
        }
        if (volumeMilliliters <= 0){
            throw  new IllegalArgumentException("Volume must be more then zero");
        }
        if (price <= 0){
            throw new IllegalArgumentException("Price must be more then zero");
        }
        if (quality < 0 || quality > 100){
            throw  new IllegalArgumentException("Quality must be between 0 and 100");
        }
        this.name = name;
        this.weightGrams = weightGrams;
        this.volumeMilliliters = volumeMilliliters;
        this.price = price;
        this.quality = quality;
    }

    public final String getName(){
        return name;
    }
    public final int getWeight(){
        return weightGrams;
    }
    public final int getVolume(){
        return volumeMilliliters;
    }
    public final int getPrice(){
        return price;
    }
    public final int getQuality(){
        return quality;
    }

    public abstract String getForm();
    public abstract String getPackaging();
}