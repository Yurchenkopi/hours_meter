package ru.yurch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Item {

    private int id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    public Item(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Item(int id, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
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

    @Override
    public String toString() {
        return "Item{"
                + "id=" + id
                + ", date=" + date
                + ", start_time=" + startTime
                + ", end_time=" + endTime
                + '}';
    }
}
