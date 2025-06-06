package ru.yurch.hours;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Item {

    private int id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean lunchBreak;

    public Item(LocalDate date, LocalTime startTime, LocalTime endTime, boolean lunchBreak) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lunchBreak = lunchBreak;
    }

    public Item(int id, LocalDate date, LocalTime startTime, LocalTime endTime, boolean lunchBreak) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lunchBreak = lunchBreak;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isLunchBreak() {
        return lunchBreak;
    }

    @Override
    public String toString() {
        return "Item{"
                + "id=" + id
                + ", date=" + date
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", lunchBreak=" + lunchBreak
                + '}';
    }
}
