package ru.yurch.hours.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yurch.hours.dto.UserDto;
import ru.yurch.hours.model.User;
import ru.yurch.hours.service.MailSenderService;
import ru.yurch.hours.service.PasswordResetTokenService;
import ru.yurch.hours.service.UserService;

import javax.validation.Valid;

@AllArgsConstructor
@Controller
public class PasswordResetTokenController {
    private PasswordEncoder passwordEncoder;
    private MailSenderService mailSenderService;
    private PasswordResetTokenService passwordResetTokenService;

    private UserService userService;

    private PasswordResetTokenService tokenService;

    private static final Logger LOG = LoggerFactory.getLogger(PasswordResetTokenController.class.getName());

    @PostMapping("/send-reset-password")
    public String sendResetEmail(@RequestParam String username, Model model, RedirectAttributes redirectAttributes) {
        LOG.info(username);
        var currentUser = userService.findByName(username);
        if (currentUser.isPresent()) {
            var passwordResetTokenOptional = tokenService.createResetToken(currentUser.get());
            if (passwordResetTokenOptional.isPresent()) {
                mailSenderService.sendResetLink(passwordResetTokenOptional.get());
                String restoreMessage = "Ссылка на восстановление пароля была отправлена на почту, указанную при регистрации";
                redirectAttributes.addFlashAttribute("restoreMessage", restoreMessage);
                return "redirect:/login";
            }
        }
        String errorMessage = "Пользователей с таким username не зарегистрировано в системе.";
        model.addAttribute("errorMessage", errorMessage);
        return "sent-restore-link-form";
    }

    @GetMapping("/send-reset-password")
    public String sendResetEmailPage(Model model) {
        model.addAttribute("username", "");
        return "sent-restore-link-form";
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam String token, Model model) {
        if (!tokenService.isValidToken(token)) {
            model.addAttribute("errorMessage", "Данная ссылка на восстановление пароля больше не действительна.");
            return "errors/404";
        }
        var userOptional = passwordResetTokenService.getUserByToken(token);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            var userDto = userService.userToUserDto(user);
            userDto.setPassword("");
            model.addAttribute("userDto", userDto);
            model.addAttribute("token", token);
        }
        return "reset-password-form";
    }
    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute @Valid UserDto userDto,
                                BindingResult result,
                                @RequestParam String token,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("org.springframework.validation.BindingResult.userDto", result);
            model.addAttribute("token", token);
            return "reset-password-form";
        }
        if (!tokenService.isValidToken(token)) {
            model.addAttribute("errorMessage", "Данная ссылка на восстановление пароля больше не действительна.");
            return "errors/404";
        }
        var userOptional = tokenService.getUserByToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userService.update(user);
        }
        tokenService.invalidateToken(token);
        String restoreMessage = "Пароль был успешно изменен.";
        redirectAttributes.addFlashAttribute("restoreMessage", restoreMessage);
        return "redirect:/login";
    }
}
