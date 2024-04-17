package ru.yurch;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface Store {
    Item add(Item item);

    Map<LocalDate, List<Item>> findByDate(LocalDate startDate, LocalDate endDate);
}
