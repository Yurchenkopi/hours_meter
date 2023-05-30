package ru.yurch;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.StringJoiner;

public class SimpleHoursCalculator implements ReportGenerator {
    @Override
    public String save(List<Item> content) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        for (Item i : content) {
            StringBuilder sb = new StringBuilder();
            sb.append(i.getDate())
                    .append(" ")
                    .append(ChronoUnit.MINUTES.between(i.getStartTime(), i.getEndTime()));
            sj.add(sb);
        }
        return sj.toString();
    }
}
