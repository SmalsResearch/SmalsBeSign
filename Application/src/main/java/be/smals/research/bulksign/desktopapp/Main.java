package be.smals.research.bulksign.desktopapp;


import java.util.stream.IntStream;

/**
 * Created by kova on 26/07/2016.
 */
public class Main {

    public static void main(String[] args) {
        printCount(10);
        System.out.println("Hello Carlos!");
        printCount(10);
    }

    public static void printCount(int lines) {
        IntStream.range(0,lines).forEach(System.out::println);
    }
}
