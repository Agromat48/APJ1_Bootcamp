package org.example;

public class Hamster extends Animal implements Herbivore {
    public Hamster(String name, int age) {
        super(name, age);
    }

    @Override
    public String chill() {
        return "I can chill for 8 hours";
    }

    @Override
    public String toString() {
        return String.format("Hamster name = %s, age = %d. %s", getName(), getAge(), chill());
    }
}
