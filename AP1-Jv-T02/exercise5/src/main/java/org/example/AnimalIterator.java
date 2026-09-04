package org.example;

import java.util.List;

public class AnimalIterator implements BaseIterator<Animal> {
    private List<Animal> animals;
    private int index;

    public AnimalIterator(List<Animal> animals) {
        this.animals = animals;
        this.index = 0;
    }

    @Override
    public Animal next() {
        return animals.get(index++);
    }

    @Override
    public boolean hasNext() {
        return index < animals.size();
    }

    @Override
    public void reset() {
        index = 0;
    }
}
