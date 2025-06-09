package ru.yurch.hours.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yurch.hours.model.Item;
import ru.yurch.hours.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {
    List<Item> findItemsByDate(LocalDate date);

    @Query("SELECT i FROM Item i WHERE i.date BETWEEN :startDate AND :endDate AND i.user = :user")
    List<Item> findItemsByDate(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("user") User user);

    boolean deleteItemsByDate(LocalDate date);
}
