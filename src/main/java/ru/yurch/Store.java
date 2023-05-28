package ru.yurch;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

public interface Store {
    Item add(Item item);

    List<Item> findByDate(LocalDate startDate, LocalDate endDate);
}
