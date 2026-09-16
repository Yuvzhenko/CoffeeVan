package com.example.coffeevan.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class CoffeeVan{
    private final long capacity;
    private final long budget;
    private final List<Coffee> cargo = new ArrayList<>();

    private long usedVolume;
    private long totalPrice;

    public CoffeeVan(long capacity, long budget){
        if (capacity <= 0){
            throw new IllegalArgumentException("Van capacity must be greater then 0");
        }
        if (budget <= 0){
            throw new IllegalArgumentException("Van budget must be greater then 0");
        }

        this.budget = budget;
        this.capacity = capacity;
    }

    public void loadAll(Collection<? extends Coffee> products){
        Objects.requireNonNull(products, "products musn't be null");

        List<Coffee> batch = List.copyOf(products);
        long newVolume = usedVolume;
        long newPrice = totalPrice;

        for (Coffee coffee : batch){
            if (coffee.getVolume() > capacity - newVolume){
                throw new IllegalArgumentException("Van capacity exceeded by: " + coffee.getName());
            }
            if (coffee.getPrice() > budget - newPrice){
                throw new IllegalArgumentException("Budget exceeded by: " + coffee.getName());
            }

            newVolume += coffee.getVolume();
            newPrice += coffee.getPrice();
        }
        cargo.addAll(batch);
        usedVolume = newVolume;
        totalPrice = newPrice;
    }

    public void sortByPriceToWeight(){
        cargo.sort((first, second) -> {
            long left = (long) first.getPrice() * second.getWeight();
            long right = (long) first.getWeight() * second.getPrice();

            return Long.compare(right, left);
        });
    }

    public List<Coffee> findByQuality (int min, int max){
        if (min < 0 || max > 100 || min > max){
            throw new IllegalArgumentException("Expected 0 <= min <= max <= 100");
        }

        return cargo.stream()
                .filter(coffee -> coffee.getQuality() >= max)
                .filter(coffee -> coffee.getQuality() <= min)
                .toList();
    }

    public List<Coffee> getCargo(){
        return List.copyOf(cargo);
    }
    public long getCapacity(){
        return capacity;
    }
    public long getBudget(){
        return budget;
    }
    public long getUsedVolume(){
        return usedVolume;
    }
    public long getTotalPrice(){
        return totalPrice;
    }
}