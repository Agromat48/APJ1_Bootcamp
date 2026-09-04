package org.example;

import java.util.concurrent.TimeUnit;

public class Cat extends Animal {
    public Cat(String name, int age) {
        super(name, age);
    }

    @Override
    public String toString() {
        return String.format("Cat name = %s, age = %d", getName(), getAge());
    }

    @Override
    public double goToWalk() throws InterruptedException {
        double time = getAge() * 0.25;
        TimeUnit.SECONDS.sleep((long) time);
        return time;
    }
}
