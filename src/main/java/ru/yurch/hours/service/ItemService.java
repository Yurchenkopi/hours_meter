package ru.yurch.hours.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yurch.hours.model.Item;
import ru.yurch.hours.model.User;
import ru.yurch.hours.repository.ItemRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    private static final Logger LOG = LoggerFactory.getLogger(ItemService.class.getName());

    public Optional<Item> save(Item item) {
        Optional<Item> rsl = Optional.empty();
        try {
            rsl = Optional.of(itemRepository.save(item));
        } catch (Exception e) {
            LOG.error("Error occurred while saving item:  " + e.getMessage());
        }
        return rsl;
    }

    public boolean deleteItemsByDate(LocalDate date) {
        boolean  rsl = false;
        try {
            rsl = itemRepository.deleteItemsByDate(date);
        } catch (Exception e) {
            LOG.error("Error occurred while deleting items:  " + e.getMessage());
        }
        return rsl;
    }

    public List<Item> findItemsByDate(LocalDate startDate, LocalDate endDate, User user) {
        List<Item> rsl = Collections.emptyList();
        try {
            rsl = itemRepository.findItemsByDate(startDate, endDate, user);
        } catch (Exception e) {
            LOG.error("Error occurred while finding items:  " + e.getMessage());
        }
        return rsl;
    }
}
