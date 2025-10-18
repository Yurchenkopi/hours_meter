package ru.yurch.hours.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yurch.hours.model.EmployerEmployee;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployerEmployeeRepository extends CrudRepository<EmployerEmployee, Integer> {
    Optional<EmployerEmployee> findById(int id);

    @Modifying
    @Transactional
    @Query("SELECT ee FROM EmployerEmployee ee JOIN ee.employer e ORDER BY e.surname ASC")
    List<EmployerEmployee> findAll();

    @Modifying
    @Transactional
    @Query("SELECT ee FROM EmployerEmployee ee WHERE ee.employer.id = ?1")
    List<EmployerEmployee> findEmployerEmployeeByEmployerId(int id);

}
