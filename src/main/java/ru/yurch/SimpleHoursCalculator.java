package ru.yurch;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.StringJoiner;

import static java.time.DayOfWeek.*;

public class SimpleHoursCalculator implements ReportGenerator {
    @Override
    public String save(List<Item> content) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        int sum = 0;
        for (Item i : content) {
            StringBuilder sb = new StringBuilder();
            int minutes = (int) ChronoUnit.MINUTES.between(i.getStartTime(), i.getEndTime());
            minutes = i.getDate().getDayOfWeek() != SATURDAY
                    && i.getDate().getDayOfWeek() != SUNDAY
                    ? minutes - 60 * 8 : minutes;
            minutes = i.isLunchBreak()
                    ? minutes - 60 : minutes;
            sum += minutes;
            sb.append(i.getDate())
                    .append(";")
                    .append((float) Math.round((float) minutes * 100 / 60) / 100)
                    .append(";");
                sj.add(sb);
            }
        sj.add("Общее время в минутах: " + sum)
                .add("Общее время в часах: " + (float) Math.round((float) sum * 100 / 60) / 100)
                .add("Общее время в днях: " + (float) Math.round((float) sum * 100 / (60 * 8)) / 100
                + System.lineSeparator());
    return sj.toString();
    }
}
