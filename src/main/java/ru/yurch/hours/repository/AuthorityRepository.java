package ru.yurch.hours.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yurch.hours.model.Authority;

@Repository
public interface AuthorityRepository extends CrudRepository<Authority, Integer> {
    Authority findByAuthority(String authority);
}
