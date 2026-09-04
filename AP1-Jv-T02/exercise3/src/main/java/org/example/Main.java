package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Animal> pets = new ArrayList<>();

        int count = sc.nextInt();
        sc.nextLine();

        readPets(sc, pets, count, 0);

        pets.stream()
                .map(pet -> {
                    if (pet.getAge() > 10) {
                        if (pet instanceof Dog) {
                            return new Dog(pet.getName(), pet.getAge() + 1);
                        } else {
                            return new Cat(pet.getName(), pet.getAge() + 1);
                        }
                    }
                    return pet;
                })
                .toList()
                .forEach(System.out::println);
    }

    static void readPets(Scanner sc, List<Animal> pets, int count, int all) {
        if (all >= count) return;

        String type, name;
        int age;

        try {
            type = sc.nextLine();
            if (!type.equals("dog") && !type.equals("cat")) {
                System.out.println("Incorrect input. Unsupported pet type");
                readPets(sc, pets, count, all + 1);
                return;
            }

            name = sc.nextLine();
            age = sc.nextInt();
            sc.nextLine();

            if (age <= 0) {
                System.out.println("Incorrect input. Age <= 0");
                readPets(sc, pets, count, all + 1);
                return;
            }

            if (type.equals("dog")) {
                pets.add(new Dog(name, age));
            } else {
                pets.add(new Cat(name, age));
            }

            readPets(sc, pets, count, all + 1);
        } catch (Exception e) {
            System.out.println("Could not parse a number. Please, try again");
            readPets(sc, pets, count, all + 1);
        }
    }
}
