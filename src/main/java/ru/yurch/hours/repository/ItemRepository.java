package ru.yurch.hours.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yurch.hours.model.Item;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {
    List<Item> findItemsByDate(LocalDate date);
    List<Item> findItemsByDate(LocalDate startDate, LocalDate endDate);

    boolean deleteItemsByDate(LocalDate date);
}
