package ru.yurch;

public class ConsoleOutput implements Output {
    @Override
    public void print(Object obj) {
        System.out.print(obj);
    }
}
