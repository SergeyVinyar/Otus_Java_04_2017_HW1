package ru.vinyarsky.hw3;

public class Main {


    public static void main(String[] args) {

        MyHashMap<Integer, String> map = new MyHashMap<>();

        for (int i = 0; i < 10000; i++) {
            map.put(i, Integer.toString(i));
        }

        System.out.println(map.containsKey(45));
        System.out.println(map.containsKey(9000));


    }
}
