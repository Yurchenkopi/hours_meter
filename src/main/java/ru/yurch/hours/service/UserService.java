package ru.yurch.hours.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yurch.hours.model.User;
import ru.yurch.hours.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class.getName());


    public Optional<User> save(User user) {
        Optional<User> rsl = Optional.empty();
        try {
            rsl = Optional.of(userRepository.save(user));
        } catch (Exception e) {
            LOG.error("Error occurred while saving user:  " + e.getMessage());
        }
        return rsl;
    }

    public boolean update(User user) {
        boolean rsl = false;
        Optional<User> currentUser = userRepository.findById(user.getId());
        LOG.info("Найден пользователь: " + currentUser);
        LOG.info("Сохраняемый пользователь: " + user);
        if (currentUser.isPresent()) {
            if (!currentUser.get().equals(user)) {
                try {
                    userRepository.save(user);
                    rsl = true;
                } catch (Exception e) {
                    LOG.error("Произошла ошибка при обновлении записи в БД: " + e.getMessage());
                }
            }
        }
        return rsl;
    }

    public Optional<User> findByName(String userName) {
        Optional<User> rsl = Optional.empty();
        try {
            rsl = userRepository.findByUsername(userName);
        } catch (Exception e) {
            LOG.error("Error occurred while finding user:  " + e.getMessage());
        }
        return rsl;
    }
}
