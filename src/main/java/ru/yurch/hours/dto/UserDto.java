package ru.yurch.hours.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yurch.hours.controller.utils.MatchFields;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MatchFields(field = "password", fieldMatch = "passwordConfirmation", message = "Пароли должны совпадать.")
public class UserDto {

    private int id;

    @NotBlank(message = "Данное поле должно быть заполнено.")
    private String username;

    private String name;

    private String patronymic;

    private String surname;

    @NotBlank(message = "Данное поле должно быть заполнено.")
    @Pattern(regexp = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}",
            message = "Введенный адрес не является адресом электронной почты.")
    private String email;

    @NotBlank(message = "Данное поле должно быть заполнено.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).+$",
            message = "Пароль должен содержать хотя бы одну строчную и прописную буквы.")
    @Size(min = 5,
            message = "Длина пароля должна быть не менее 5 символов.")
    private String password;

    @NotBlank(message = "Данное поле должно быть заполнено.")
    private String passwordConfirmation;

    private boolean employer;
}
