package ru.yurch;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportGenerator {
    String save(Map<LocalDate, List<Item>> content);
}
