package ru.yurch.hours.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yurch.hours.model.ReportSetting;
import ru.yurch.hours.model.User;
import ru.yurch.hours.repository.AuthorityRepository;
import ru.yurch.hours.service.AuthorityService;
import ru.yurch.hours.service.UserService;

@Controller
@AllArgsConstructor
public class RegController {

    private final PasswordEncoder encoder;
    private final UserService userService;
    private final AuthorityService authorityService;

    @PostMapping("/reg")
    public String regSave(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        if (!userService.isEmail(user.getEmail())) {
            model.addAttribute("errorMessage", "Введен неверный адрес электронной почты.");
            return "register";
        }
        user.setEnabled(true);
        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getAuthority().getAuthority().equals("ROLE_USER")) {
            user.setAuthority(authorityService.findByAuthority("ROLE_USER"));
        } else if (user.getAuthority().getAuthority().equals("ROLE_EMPLOYER")) {
            user.setAuthority(authorityService.findByAuthority("ROLE_EMPLOYER"));
        }
        user.setReportSetting(new ReportSetting());
        var savedUser = userService.save(user);
        if (savedUser.isEmpty()) {
            if (userService.findByName(user.getUsername()).isPresent()) {
                model.addAttribute("errorMessage", "Пользователь с таким username уже зарегистрирован.");
            } else if (userService.findByEmail(user.getEmail()).isPresent()) {
                model.addAttribute("errorMessage", "Пользователь с таким email уже зарегистрирован.");
            } else {
                model.addAttribute("errorMessage", "Произошла ошибка при регистрации нового пользователя. Повторите попытку позже.");
            }
            return "register";
        }
        redirectAttributes.addFlashAttribute("regMessage", "Новый пользователь успешно зарегистрирован.");
        return "redirect:/login";
    }

    @GetMapping("/reg")
    public String regPage() {
        return "register";
    }
}
