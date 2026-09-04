package org.example;

public class GuineaPig extends Animal implements Herbivore {
    public GuineaPig(String name, int age) {
        super(name, age);
    }

    @Override
    public String chill() {
        return "I can chill for 12 hours";
    }

    @Override
    public String toString() {
        return String.format("GuineaPig name = %s, age = %d. %s", getName(), getAge(), chill());
    }
}
