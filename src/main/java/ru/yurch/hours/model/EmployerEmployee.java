package ru.yurch.hours.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "employers_employees")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EmployerEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @ManyToOne
    @JoinColumn(name = "employers_id")
    private User employer;

    @ManyToOne
    @JoinColumn(name = "employees_id")
    private User employee;
}