package ru.yurch.hours.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private int id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean lunchBreak;

    private boolean isExtraHoursOnly;

    private String remark;

    private float minutes;

}

