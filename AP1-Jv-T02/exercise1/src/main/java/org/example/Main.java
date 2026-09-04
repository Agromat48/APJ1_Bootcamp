package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Animal> pets = new ArrayList<>();
        int count = sc.nextInt();
        sc.nextLine();
        int all = 0;

        while (all < count) {
            if (!sc.hasNext()) break;

            String type, name;
            int age;
            double mass;

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

                mass = sc.nextDouble();
                sc.nextLine();

                if (mass <= 0) {
                    System.out.println("Incorrect input. Mass <= 0");
                    ++all;
                    continue;
                }

                if (type.equals("dog")) {
                    pets.add(new Dog(name, age, mass));
                } else {
                    pets.add(new Cat(name, age, mass));
                }

                ++all;
            } catch (Exception e) {
                System.out.println("Could not parse a number. Please, try again");
            }
        }

        for (var el : pets) {
            System.out.println(el);
        }
    }
}
