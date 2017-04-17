package ru.vinyarsky.hw3;

/*
 * Реализован класс MyHashMap.
 * Generic и поддерживает перехеширование.
 * Есть тесты.
 *
 * В main реализовано сравнение по производительности со штатным HashMap.
 */

import java.util.*;
import java.util.function.Consumer;

public class Main {

    private static HashMap<Integer, String> map = new HashMap<>();
    private static MyHashMap<Integer, String> myMap = new MyHashMap<>();

    public static void main(String[] args) {
        measure("Добавление записей", (m) -> {
                    for (int i = 0; i < 25_000; i++) {
                        m.put(i, Integer.toString(i));
                    }
                });

        measure("keySet()", (m) -> {
            m.keySet();
        });

        measure("values()", (m) -> {
            m.values();
        });

        measure("entrySet()", (m) -> {
            m.entrySet();
        });

        measure("Удаление записей", (m) -> {
            for (int i = 0; i < 25_000; i++) {
                m.remove(i);
            }
        });
    }

    private static void measure(String title, Consumer<Map<Integer, String>> run) {
        System.out.println("----------------------");
        System.out.println(title);

        long before;
        long after;

        before = System.nanoTime();
        run.accept(map);
        after = System.nanoTime();
        System.out.println("\tHashMap:\t" + (after - before) / 1000 + " мкс");

        before = System.nanoTime();
        run.accept(myMap);
        after = System.nanoTime();
        System.out.println("\tMyHashMap:\t" + (after - before) / 1000 + " мкс");
        System.out.println("----------------------");
        System.out.println();
    }
}
