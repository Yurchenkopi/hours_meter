package ru.yurch;

import java.time.LocalDate;
import java.util.List;

public interface Store {
    Item add(Item item);

    List<Item> findByDate(LocalDate startDate, LocalDate endDate);

    boolean deleteByDate(LocalDate date);
}
