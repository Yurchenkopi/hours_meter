CREATE TABLE report_settings (
                             id SERIAL PRIMARY KEY,
                             date_column BOOL,
                             start_time_column BOOL,
                             end_time_column BOOL,
                             lunch_break_column BOOL,
                             extra_hours_only_column BOOL,
                             remark_column BOOL,
                             hours_column BOOL
);