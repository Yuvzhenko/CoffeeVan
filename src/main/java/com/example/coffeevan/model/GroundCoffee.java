package com.example.coffeevan.model;

public final class GroundCoffee extends Coffee{
    public GroundCoffee(String name, int weightGrams, int volumeMilliliters, int price, int quality){
        super(name, weightGrams, volumeMilliliters, price, quality);
    }

    @Override
    public String getForm(){
        return "Ground";
    }

    @Override
    public String getPackaging(){
        return "Bag";
    }
}