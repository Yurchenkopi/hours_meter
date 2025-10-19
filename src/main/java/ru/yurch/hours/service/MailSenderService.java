package ru.yurch.hours.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.yurch.hours.model.PasswordResetToken;

@AllArgsConstructor
@Service
public class MailSenderService {
    private JavaMailSender mailSender;

    private static final Logger LOG = LoggerFactory.getLogger(MailSenderService.class.getName());

    public void sendResetLink(PasswordResetToken passwordResetToken) {
        String resetUrl = "http://45.142.36.220:8080/reset-password?token=" + passwordResetToken.getToken();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(passwordResetToken.getUser().getEmail());
        message.setSubject("Восстановление пароля");
        message.setText("Чтобы сбросить пароль, перейдите по ссылке: " + resetUrl);
        try {
            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            LOG.error("Ошибка аутентификации при отправке письма: {}", e.getMessage());
        } catch (Exception e) {
            LOG.error("Произошла ошибка при отправке письма: {}", e.getMessage());
        }
    }
}
