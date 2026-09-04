package org.example;

public class Cat extends Animal {
    public Cat(String name, int age, double mass) {
        super(name, age, mass);
    }

    @Override
    public double getFeedInfoKg() {
        return getMass() * 0.1;
    }

    @Override
    public String toString() {
        return String.format("Cat name = %s, age = %d, mass = %.2f, feed = %.2f",
                getName(), getAge(), getMass(), getFeedInfoKg());
    }
}
