package ru.yurch.hours.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @ManyToOne
    @JoinColumn(name = "authority_id")
    private Authority authority;

    private Long chatId;

    private String username;

    private String email;

    private String name;

    private String patronymic;

    private String surname;

    private String password;

    private boolean enabled;
}
