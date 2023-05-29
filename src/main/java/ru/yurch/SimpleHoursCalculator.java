package ru.yurch;

import java.time.LocalTime;
import java.util.List;
import java.util.StringJoiner;

public class SimpleHoursCalculator implements ReportGenerator {
    @Override
    public String save(List<Item> content) {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        for (Item i : content) {
            sj.add(i.getEndTime() - i.getStartTime())
        }
        return "";
    }
}
