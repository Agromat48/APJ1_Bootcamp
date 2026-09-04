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

        long programStart = System.nanoTime();
        List<String> results = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < pets.size(); i++) {
            final int index = i;
            Thread thread = new Thread(() -> {
                try {
                    double start = (System.nanoTime() - programStart) / 1_000_000_000.0;
                    pets.get(index).goToWalk();
                    double end = (System.nanoTime() - programStart) / 1_000_000_000.0;
                    synchronized (results) {
                        results.add(String.format("%s, start time = %.2f, end time = %.2f",
                                pets.get(index), start, end));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        results.sort((a, b) -> {
            double endA = Double.parseDouble(a.substring(a.lastIndexOf("end time = ") + 11));
            double endB = Double.parseDouble(b.substring(b.lastIndexOf("end time = ") + 11));
            return Double.compare(endA, endB);
        });

        for (String result : results) {
            System.out.println(result);
        }
    }
}
