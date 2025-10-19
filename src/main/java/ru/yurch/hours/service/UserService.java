package ru.yurch.hours.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yurch.hours.dto.UserDto;
import ru.yurch.hours.model.ReportSetting;
import ru.yurch.hours.model.User;
import ru.yurch.hours.repository.AuthorityRepository;
import ru.yurch.hours.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

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

    public List<User> findAllEmployers() {
        List<User> rsl = Collections.emptyList();
        try {
            rsl = userRepository.findAllEmployers();
        } catch (Exception e) {
            LOG.error("Error occurred while finding employers role users: " + e.getMessage());
        }
        return rsl;
    }

    public List<User> findAllEmployees() {
        List<User> rsl = Collections.emptyList();
        try {
            rsl = userRepository.findAllEmployees();
        } catch (Exception e) {
            LOG.error("Error occurred while finding employees role users: " + e.getMessage());
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

    public boolean isEmployee(User user) {
        return user.getAuthority().getAuthority().equals("ROLE_USER");
    }

    public boolean isEmployer(User user) {
        return user.getAuthority().getAuthority().equals("ROLE_EMPLOYER");
    }

    public boolean isAdmin(User user) {
        return user.getAuthority().getAuthority().equals("ROLE_ADMIN");
    }

    public User userDtoToUser(UserDto userDto) {
        var user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setName(userDto.getName());
        user.setPatronymic(userDto.getPatronymic());
        user.setSurname(userDto.getSurname());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        if (userDto.isEmployer()) {
            user.setAuthority(authorityRepository.findByAuthority("ROLE_EMPLOYER"));
        } else {
            user.setAuthority(authorityRepository.findByAuthority("ROLE_USER"));
        }
        var defaultReportSetting = new ReportSetting(
                true,
                true,
                true,
                false,
                false,
                false,
                true);
        user.setReportSetting(defaultReportSetting);
        if (userDto.getName().isEmpty()) {
            user.setName(String.format("%s_name", user.getUsername()));
        }
        if (userDto.getPatronymic().isEmpty()) {
            user.setPatronymic(String.format("%s_patronymic", user.getUsername()));
        }
        if (userDto.getSurname().isEmpty()) {
            user.setSurname(String.format("%s_surname", user.getUsername()));
        }
        return user;
    }

    public UserDto userToUserDto(User user) {
        var userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setName(user.getName());
        userDto.setPatronymic(user.getPatronymic());
        userDto.setSurname(user.getSurname());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        if (authorityRepository.findByAuthority("ROLE_USER").equals(user.getAuthority())) {
            userDto.setEmployer(false);
        } else if (authorityRepository.findByAuthority("ROLE_EMPLOYER").equals(user.getAuthority())) {
            userDto.setEmployer(true);
        }
        return userDto;
    }
}
