package ru.yurch.hours.model;

import lombok.*;
import ru.yurch.hours.dto.ItemDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private Map<LocalDate, List<ItemDto>> content;

    private float timeInMinutes;
}
