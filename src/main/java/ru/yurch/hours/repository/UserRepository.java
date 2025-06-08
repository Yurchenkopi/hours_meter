package ru.yurch.hours.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yurch.hours.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

}
