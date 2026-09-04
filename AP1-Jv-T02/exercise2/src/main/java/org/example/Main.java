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

        while(all < count) {
            if (!sc.hasNext()) break;

            String type, name;
            int age;

            try {
                type = sc.nextLine();
                if (!type.equals("dog") && !type.equals("cat")
                        && !type.equals("hamster") && !type.equals("guinea")) {
                    System.out.println("Incorrect input. Unsupported pet type");
                    ++all;
                    continue;
                }

                name = sc.nextLine();
                age = sc.nextInt();
                sc.nextLine();

                if(age <= 0) {
                    System.out.println("Incorrect input. Age <= 0");
                    ++all;
                    continue;
                }

                switch (type) {
                    case "dog" -> pets.add(new Dog(name, age));
                    case "cat" -> pets.add(new Cat(name, age));
                    case "hamster" -> pets.add(new Hamster(name, age));
                    case "guinea" -> pets.add(new GuineaPig(name, age));
                }

                ++all;
            }
            catch (Exception e) {
                System.out.println("Could not parse a number. Please, try again");
            }
        }

        for(var el : pets) {
            if(el instanceof Herbivore) {
                System.out.println(el);
            }
        }

        for(var el : pets) {
            if(el instanceof Omnivore) {
                System.out.println(el);
            }
        }
    }
}
