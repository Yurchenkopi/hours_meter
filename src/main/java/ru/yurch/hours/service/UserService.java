package ru.yurch.hours.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yurch.hours.model.User;
import ru.yurch.hours.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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

    public Optional<User> findById(int id) {
        Optional<User> rsl = Optional.empty();
        try {
            rsl = userRepository.findById(id);
        } catch (Exception e) {
            LOG.error("Error occurred while finding user:  " + e.getMessage());
        }
        return rsl;
    }

    public Optional<User> findByEmail(String email) {
        Optional<User> rsl = Optional.empty();
        try {
            rsl = userRepository.findByEmail(email);
        } catch (Exception e) {
            LOG.error("Error occurred while finding user:  " + e.getMessage());
        }
        return rsl;
    }

    public boolean isEmail(String email) {
        Pattern pattern = Pattern.compile("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
        return pattern.matcher(email).matches();
    }

    public List<User> findBindedEmployees(int employerId) {
        return userRepository.findBindedEmployees(employerId);
    }
}
