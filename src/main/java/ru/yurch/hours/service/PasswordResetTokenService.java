package ru.yurch.hours.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yurch.hours.model.PasswordResetToken;
import ru.yurch.hours.model.User;
import ru.yurch.hours.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordResetTokenService {
    private PasswordResetTokenRepository tokenRepository;

    private static final Logger LOG = LoggerFactory.getLogger(PasswordResetTokenService.class.getName());

    public Optional<PasswordResetToken> createResetToken(User user) {

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        Optional<PasswordResetToken> rsl = Optional.empty();
        try {
            rsl = Optional.of(tokenRepository.save(resetToken));
        } catch (Exception e) {
            LOG.error("Error occurred while creating password reset token:  " + e.getMessage());
        }
        return rsl;
    }
    public Optional<PasswordResetToken> getToken(String token) {
        return tokenRepository.findByToken(token);
    }
    public boolean isValidToken(String token) {
        return getToken(token)
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .isPresent();
    }
    public Optional<User> getUserByToken(String token) {
        return getToken(token)
                .map(PasswordResetToken::getUser);
    }
    public void invalidateToken(String token) {
        getToken(token).ifPresent(tokenRepository::delete);
    }
}
