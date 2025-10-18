package ru.yurch.hours.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yurch.hours.dto.EmployerEmployeeDto;
import ru.yurch.hours.model.EmployerEmployee;
import ru.yurch.hours.repository.EmployerEmployeeRepository;
import ru.yurch.hours.repository.UserRepository;

import java.util.*;

@Service
@AllArgsConstructor
public class EmployerEmployeeService {
    private final EmployerEmployeeRepository employerEmployeeRepository;

    private static final Logger LOG = LoggerFactory.getLogger(EmployerEmployeeService.class.getName());

    public Optional<EmployerEmployee> save(EmployerEmployee employerEmployee) {
        Optional<EmployerEmployee> rsl = Optional.empty();
        try {
            rsl = Optional.of(employerEmployeeRepository.save(employerEmployee));
        } catch (Exception e) {
            LOG.error("Error occurred while binding employee:  " + e.getMessage());
        }
        return rsl;
    }

    public Optional<EmployerEmployee> findById(int id) {
        Optional<EmployerEmployee> rsl = Optional.empty();
        try {
            rsl = employerEmployeeRepository.findById(id);
        } catch (Exception e) {
            LOG.error("Error occurred while finding binded user:  " + e.getMessage());
        }
        return rsl;
    }

    public List<EmployerEmployee> findAll() {
        List<EmployerEmployee> rsl = Collections.emptyList();
        try {
            rsl = employerEmployeeRepository.findAll();
        } catch (Exception e) {
            LOG.error("Error occurred while finding binding users: " + e.getMessage());
        }
        return rsl;
    }

    public boolean delete(EmployerEmployee employerEmployee) {
        boolean rsl = false;
        Optional<EmployerEmployee> currentEmployerEmployee = employerEmployeeRepository.findById(employerEmployee.getId());
        LOG.info("Найдена запись: " + currentEmployerEmployee);
        LOG.info("Удаляемая запись: " + employerEmployee);
        if (currentEmployerEmployee.isPresent()) {
            try {
                employerEmployeeRepository.delete(employerEmployee);
                rsl = true;
            } catch (Exception e) {
                LOG.error("Произошла ошибка при отвязывании учётной записи: " + e.getMessage());
            }
        }
        return rsl;
    }

    public EmployerEmployeeDto eeToEeDto(EmployerEmployee employerEmployee) {
        return new EmployerEmployeeDto(
                employerEmployee.getId(),
                employerEmployee.getEmployer().getId(),
                employerEmployee.getEmployer().getSurname(),
                employerEmployee.getEmployee().getId(),
                employerEmployee.getEmployee().getSurname()
        );
    }
}
