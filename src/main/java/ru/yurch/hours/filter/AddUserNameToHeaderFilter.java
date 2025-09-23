package ru.yurch.hours.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.yurch.hours.model.Authority;
import ru.yurch.hours.model.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AddUserNameToHeaderFilter extends HttpFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AddUserNameToHeaderFilter.class.getName());


    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = new User();
        LOG.info("Principal is {}", authentication.getPrincipal());
        if (!authentication.getPrincipal().equals("anonymousUser")) {
            var userDetails = (UserDetails) authentication.getPrincipal();
            user.setUsername(userDetails.getUsername());
            String role = userDetails.getAuthorities().stream().findFirst().get().getAuthority();
            Authority authority = new Authority();
            authority.setAuthority(role);
            user.setAuthority(authority);
        } else {
            user.setUsername("Гость");
        }
        LOG.info("User added to session is {}", user);
//        request.setAttribute("user", user);
        request.getSession().setAttribute("user", user);
        filterChain.doFilter(request, response);
    }
}