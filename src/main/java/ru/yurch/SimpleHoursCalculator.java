package ru.yurch;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.StringJoiner;

public class SimpleHoursCalculator implements ReportGenerator {
    @Override
    public String save(List<Item> content) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        int sum = 0;
        for (Item i : content) {
            StringBuilder sb = new StringBuilder();
            int minutes = (int) ChronoUnit.MINUTES.between(i.getStartTime(), i.getEndTime());
            minutes = i.isLunchBreak() ? minutes - 60 * 9 : minutes;
            sum += minutes;
            sb.append(i.getDate())
                    .append(";")
                    .append(minutes)
                    .append(";");
                sj.add(sb);
            }
        sj.add("Общее время в минутах: " + sum).add("Общее время в днях: " + sum / (60 * 8));
    return sj.toString();
    }
}
