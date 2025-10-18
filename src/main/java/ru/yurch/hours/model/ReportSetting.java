package ru.yurch.hours.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "report_settings")
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ReportSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date_column")
    private boolean dateColumn;

    @Column(name = "start_time_column")
    private boolean startTimeColumn;

    @Column(name = "end_time_column")
    private boolean endTimeColumn;

    @Column(name = "lunch_break_column")
    private boolean lunchBreakColumn;

    @Column(name = "extra_hours_only_column")
    private boolean extraHoursOnlyColumn;

    @Column(name = "remark_column")
    private boolean remarkColumn;

    @Column(name = "hours_column")
    private boolean hoursColumn;

    public ReportSetting(boolean dateColumn, boolean startTimeColumn, boolean endTimeColumn, boolean lunchBreakColumn, boolean extraHoursOnlyColumn, boolean remarkColumn, boolean hoursColumn) {
        this.dateColumn = dateColumn;
        this.startTimeColumn = startTimeColumn;
        this.endTimeColumn = endTimeColumn;
        this.lunchBreakColumn = lunchBreakColumn;
        this.extraHoursOnlyColumn = extraHoursOnlyColumn;
        this.remarkColumn = remarkColumn;
        this.hoursColumn = hoursColumn;
    }
}
