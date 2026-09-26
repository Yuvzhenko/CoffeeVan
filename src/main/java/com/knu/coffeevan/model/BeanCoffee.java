package com.knu.coffeevan.model;

public final class BeanCoffee extends Coffee{
    public BeanCoffee(String name, int weightGrams, int volumeMilliliters, int price, int quality){
        super (name, weightGrams, volumeMilliliters, price, quality);
    }

    @Override
    public String getForm(){
        return "Beans";
    }

    @Override 
    public String getPackaging(){
        return "Bag";
    }
}