package ru.yurch.hours.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yurch.hours.dto.UserDto;
import ru.yurch.hours.service.AuthorityService;
import ru.yurch.hours.service.UserService;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class RegController {

    private final PasswordEncoder encoder;
    private final UserService userService;

    @PostMapping("/reg")
    public String register(@ModelAttribute @Valid UserDto userDto, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        System.out.println(userDto);
        if (result.hasErrors()) {
            model.addAttribute("org.springframework.validation.BindingResult.userDto", result);
            return "register";
        }
        var user = userService.userDtoToUser(userDto);
        user.setEnabled(true);
        user.setPassword(encoder.encode(user.getPassword()));
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
    public String regPage(Model model) {
        var defaultUserDto = new UserDto();
        model.addAttribute("userDto", defaultUserDto);
        return "register";
    }
}
