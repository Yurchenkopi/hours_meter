package ru.yurch.hours.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yurch.hours.model.Item;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {
}
