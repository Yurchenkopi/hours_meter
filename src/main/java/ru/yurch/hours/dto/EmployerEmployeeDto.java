package ru.yurch.hours.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerEmployeeDto {
    private int eeId;

    private int employerId;

    private String employerSurname;

    private int employeeId;

    private String employeeSurname;

}
