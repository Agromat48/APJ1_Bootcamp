package org.example;

public abstract class Animal {
    private final String name;
    private final int age;
    private final double mass;

    public Animal(String name, int age, double mass) {
        this.name = name;
        this.age = age;
        this.mass = mass;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getMass() {
        return mass;
    }

    public abstract double getFeedInfoKg();
}
