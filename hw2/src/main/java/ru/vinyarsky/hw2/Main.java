package ru.vinyarsky.hw2;

public class Main {

    public static void main(String[] args) throws Exception {
        new Experiment("ЭКСПЕРИМЕНТ: Один Object")
                .setSyclesCount(10)
                .addObject(Object.class, Object.class, "")
                .execute(false);

        new Experiment("ЭКСПЕРИМЕНТ: Два Object'а")
                .setSyclesCount(10)
                .addObject(Object.class, Object.class, "")
                .addObject(Object.class, Object.class, "")
                .execute(false);
    }
}
