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

        int all = 0;

        while (all < count) {
            if (!sc.hasNext()) break;

            String type, name;
            int age;

            try {
                type = sc.nextLine();
                if (!type.equals("dog") && !type.equals("cat")) {
                    System.out.println("Incorrect input. Unsupported pet type");
                    ++all;
                    continue;
                }

                name = sc.nextLine();
                age = sc.nextInt();
                sc.nextLine();

                if (age <= 0) {
                    System.out.println("Incorrect input. Age <= 0");
                    ++all;
                    continue;
                }

                if (type.equals("dog")) {
                    pets.add(new Dog(name, age));
                } else {
                    pets.add(new Cat(name, age));
                }

                ++all;
            } catch (Exception e) {
                System.out.println("Could not parse a number. Please, try again");
            }
        }

        AnimalIterator iterator = new AnimalIterator(pets);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
