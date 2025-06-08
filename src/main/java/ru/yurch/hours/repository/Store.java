package ru.yurch.hours.repository;

import ru.yurch.hours.model.Item;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface Store {
    Item add(Item item);

    Map<LocalDate, List<Item>> findByDate(LocalDate startDate, LocalDate endDate);

    boolean deleteByDate(LocalDate date);
}
