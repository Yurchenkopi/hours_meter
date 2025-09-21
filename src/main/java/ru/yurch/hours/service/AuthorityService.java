package ru.yurch.hours.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yurch.hours.model.Authority;
import ru.yurch.hours.repository.AuthorityRepository;

@Service
@AllArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;

   public Authority findByAuthority(String authority) {
       return authorityRepository.findByAuthority(authority);
   }
}
