package ru.yurch.hours.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "report_settings")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReportSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    private boolean date_column;

    private boolean startTime_column;

    private boolean endTime_column;

    private boolean lunchBreak_column;

    private boolean extraHoursOnly_column;

    private boolean remark_column;

    private boolean hours_column;
}
