package ru.yurch.hours.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ru.yurch.hours.model.User;
import ru.yurch.hours.repository.AuthorityRepository;
import ru.yurch.hours.service.UserService;

@Controller
@AllArgsConstructor
public class RegController {

    private final PasswordEncoder encoder;
    private final UserService userService;
    private final AuthorityRepository authorities;

    @PostMapping("/reg")
    public String regSave(@ModelAttribute User user, Model model) {
        user.setEnabled(true);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setAuthority(authorities.findByAuthority("ROLE_USER"));
        var savedUser = userService.save(user);
        if (savedUser.isEmpty()) {
            model.addAttribute("errorMessage", "Пользователь с таким username уже зарегистрирован.");
            return "register";
        }
        return "redirect:/login";
    }

    @GetMapping("/reg")
    public String regPage() {
        return "register";
    }
}
