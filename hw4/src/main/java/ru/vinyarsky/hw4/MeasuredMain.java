package ru.vinyarsky.hw4;

import java.util.ArrayList;
import java.util.List;

public class MeasuredMain {

    public static final String STARTED = "Started";

    public static void main(String[] args) throws Exception {
        System.out.println(STARTED);

        List<Object> list = new ArrayList<>();
        while (true) {
            list.add(new Object());
            list.add(new Object());
            list.add(new Object());
            list.add(new Object());
            list.add(new Object());
            list.add(new Object());
            list.add(new Object());
            list.remove(0);
        }
    }
}
