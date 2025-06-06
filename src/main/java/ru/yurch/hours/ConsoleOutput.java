package ru.yurch.hours;

public class ConsoleOutput implements Output {
    @Override
    public void print(Object obj) {
        System.out.println("-".repeat(50));
        System.out.print(obj);
        System.out.println("-".repeat(50));
    }
}
