package ru.vinyarsky.hw1;

import org.apache.commons.csv.*;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello Otus! Preparing to make a csv-output...");
        System.out.println();

        CSVPrinter printer = new CSVPrinter(System.out, CSVFormat.DEFAULT);
        printer.printRecord("String_one", "String two", 3, 4.5);
        printer.printRecord(6, 7, 8);

        System.out.println();
        System.out.println("Completed!");
    }
}
