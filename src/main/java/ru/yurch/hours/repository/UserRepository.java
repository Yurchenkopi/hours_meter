package ru.yurch.hours.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yurch.hours.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    Optional<User> findById(int id);
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("SELECT ee.employee FROM EmployerEmployee ee WHERE ee.employer.id = ?1")
    List<User> findBindedEmployees(int employerId);

    @Modifying
    @Transactional
    @Query("SELECT u FROM User u JOIN FETCH u.authority a WHERE a.authority = 'ROLE_EMPLOYER'")
    List<User> findAllEmployers();

    @Modifying
    @Transactional
    @Query("SELECT u FROM User u JOIN FETCH u.authority a WHERE a.authority = 'ROLE_USER'")
    List<User> findAllEmployees();
}
