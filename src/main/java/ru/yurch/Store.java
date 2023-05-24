package main.java.ru.yurch;

import java.util.List;
import java.util.function.Predicate;

public interface Store {
    void add(Item item);

    List<Item> findBy(Predicate<Item> condition);
}
