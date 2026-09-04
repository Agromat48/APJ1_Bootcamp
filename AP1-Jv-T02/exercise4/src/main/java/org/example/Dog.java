package org.example;

import java.util.concurrent.TimeUnit;

public class Dog extends Animal {
    public Dog(String name, int age) {
        super(name, age);
    }

    @Override
    public String toString() {
        return String.format("Dog name = %s, age = %d", getName(), getAge());
    }

    @Override
    public double goToWalk() throws InterruptedException {
        double time = getAge() * 0.5;
        TimeUnit.SECONDS.sleep((long) time);
        return time;
    }
}
