package ru.yurch.hours.service;

import ru.yurch.hours.model.Item;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportGenerator {
    String save(Map<LocalDate, List<Item>> content);
}
