package main.java.ru.yurch;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Item {

    private int id;

    private LocalDateTime date;

    private LocalTime startTime;

    private LocalTime endTime;

    public Item(int id, LocalDateTime date, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
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
