package ru.yurch.hours.service;

import ru.yurch.hours.model.Item;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.time.temporal.ChronoUnit;

import static java.time.DayOfWeek.*;

public class CsvReportGenerator implements ReportGenerator {
    @Override
    public String save(Map<LocalDate, List<Item>> content) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        float sum = 0;
        for (List<Item> list : content.values()) {
            float minutes = 0;
            LocalDate ld = list.get(0).getDate();
            minutes = ld.getDayOfWeek() != SATURDAY
                    && ld.getDayOfWeek() != SUNDAY
                    ? minutes - 60 * 8 : minutes;
            if (list.size() > 1) {
                float delta = minutes / list.size();
                for (Item i : list) {
                    minutes = ChronoUnit.MINUTES.between(i.getStartTime(), i.getEndTime()) + delta;
                    minutes = i.isLunchBreak()
                            ? minutes - 60 : minutes;
                    sj.add(printInfo(i,  minutes));
                    sum += minutes;
                }
            } else {
                for (Item i : list) {
                    minutes += ChronoUnit.MINUTES.between(i.getStartTime(), i.getEndTime());
                    minutes = i.isLunchBreak()
                            ? minutes - 60 : minutes;
                    sj.add(printInfo(i, minutes));
                    sum += minutes;
                }
            }
        }
        sj.add("Общее время в минутах: ;" + sum + ";")
                .add("Общее время в часах: ;" + (float) Math.round(sum * 100 / 60) / 100 + ";")
                .add("Общее время в днях: ;" + (float) Math.round(sum * 100 / (60 * 8)) / 100
                        + ";" + System.lineSeparator());
    return sj.toString();
    }

    private StringBuilder printInfo(Item item, float minutes) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getDate())
                .append(";")
                .append(item.getStartTime())
                .append(";")
                .append(item.getEndTime())
                .append(";")
                .append((float) Math.round(minutes * 100 / 60) / 100)
                .append(";");
        return sb;
    }
}
