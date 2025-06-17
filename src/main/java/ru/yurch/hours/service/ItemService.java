package ru.yurch.hours.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yurch.hours.dto.ItemDto;
import ru.yurch.hours.model.Item;
import ru.yurch.hours.model.User;
import ru.yurch.hours.repository.ItemRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

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

    public ItemDto itemToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setDate(item.getDate());
        itemDto.setStartTime(item.getStartTime());
        itemDto.setEndTime(item.getEndTime());
        itemDto.setLunchBreak(item.isLunchBreak());
        itemDto.setRemark(item.getRemark());
        return itemDto;
    }

    public Map<LocalDate, List<ItemDto>> convertToItemsDto(List<Item> items) {
        Map<LocalDate, List<ItemDto>> content = new TreeMap<>();
        for (Item tempItem : items) {
            content.computeIfAbsent(tempItem.getDate(), k -> new ArrayList<>());
            if (content.get(tempItem.getDate()) == null) {
                content.get(tempItem.getDate()).add(itemToItemDto(tempItem));
            }
            content.computeIfPresent(tempItem.getDate(), (k, v) -> {
                v.add(itemToItemDto(tempItem));
                return v;
            });
        }
        return content;
    }

    public Map<LocalDate, List<ItemDto>> updateExtraTime(List<Item> items) {
        Map<LocalDate, List<ItemDto>> content = convertToItemsDto(items);
        for (List<ItemDto> list : content.values()) {
            float minutes = 0;
            LocalDate ld = list.getFirst().getDate();
            minutes = ld.getDayOfWeek() != SATURDAY
                    && ld.getDayOfWeek() != SUNDAY
                    && !list.getFirst().isExtraHoursOnly()
                    ? minutes - 60 * 8 : minutes;
            if (list.size() > 1) {
                float delta = minutes / list.size();
                for (ItemDto i : list) {
                    minutes = ChronoUnit.MINUTES.between(i.getStartTime(), i.getEndTime()) + delta;
                    minutes = i.isLunchBreak()
                            ? minutes - 60 : minutes;
                    i.setMinutes(minutes);
                }
            } else {
                for (ItemDto i : list) {
                    minutes += ChronoUnit.MINUTES.between(i.getStartTime(), i.getEndTime());
                    minutes = i.isLunchBreak()
                            ? minutes - 60 : minutes;
                    i.setMinutes(minutes);
                }
            }
        }
        return content;
    }
}
