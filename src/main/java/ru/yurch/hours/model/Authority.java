package ru.yurch.hours.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "authorities")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    private String authority;
}