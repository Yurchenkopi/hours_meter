package ru.yurch.hours.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yurch.hours.model.User;
import ru.yurch.hours.service.ReportSettingService;
import ru.yurch.hours.service.UserService;

@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ReportSettingService reportSettingService;

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class.getName());

    @GetMapping("/info")
    public String getByDate(
            Model model,
            @SessionAttribute(name = "user") User user
    ) {
        var currentUser = userService.findByName(user.getUsername());
        if (currentUser.isPresent()) {
            model.addAttribute("user", currentUser.get());
        }
        return "users/info";
    }

    @GetMapping("/reportSetting")
    public String getReportSetting(
            Model model,
            @SessionAttribute(name = "user") User user
    ) {
        var currentUser = userService.findByName(user.getUsername());
        if (currentUser.isPresent()) {
            model.addAttribute("user", currentUser.get());
        }
        return "users/reportSetting";
    }


    @PostMapping("/updateInfo")
    public String updateUserInfo(
            @SessionAttribute(name = "user") User user,
            @ModelAttribute User modUser,
            Model model
    ) {
        var sessUser = userService.findByName(user.getUsername()).get();
        var tempUser = new User(
                sessUser.getId(),
                sessUser.getAuthority(),
                sessUser.getChatId(),
                sessUser.getUsername(),
                sessUser.getEmail(),
                modUser.getName(),
                modUser.getPatronymic(),
                modUser.getSurname(),
                sessUser.getPassword(),
                sessUser.isEnabled(),
                sessUser.getReportSetting()
        );
        var isUpdated = userService.update(tempUser);
        if (!isUpdated) {
            model.addAttribute("errorMessage", "Не удалось произвести редактирование личных данных пользователя.");
            return "errors/404";
        }
        var message = String.format(
                "Личные данные пользователя %s были успешно изменены.",
                user.getUsername());
        model.addAttribute("message", message);
        return "messages/message";
    }

    @PostMapping("/updateReportSetting")
    public String updateReportSetting(
            @SessionAttribute(name = "user") User user,
            @ModelAttribute User modUser,
            Model model
    ) {
        var sessUser = userService.findByName(user.getUsername()).get();
        var tempUser = new User(
                sessUser.getId(),
                sessUser.getAuthority(),
                sessUser.getChatId(),
                sessUser.getUsername(),
                sessUser.getEmail(),
                sessUser.getName(),
                sessUser.getPatronymic(),
                sessUser.getSurname(),
                sessUser.getPassword(),
                sessUser.isEnabled(),
                modUser.getReportSetting()
        );
        var isUpdated = userService.update(tempUser);
        if (!isUpdated) {
            model.addAttribute("errorMessage", "Не удалось обновить параметры отчета");
            return "errors/404";
        }
        var message = String.format(
                "Настройки отчета пользователя %s были успешно изменены.",
                user.getUsername());
        model.addAttribute("message", message);
        return "messages/message";
    }

}
