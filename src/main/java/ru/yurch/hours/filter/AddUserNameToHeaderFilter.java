package ru.yurch.hours.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
        var principal = authentication.getPrincipal();
        LOG.info("Principal is {}", principal);
        if (!"anonymousUser".equals(principal)) {
            request.setAttribute("user", principal);
        }
        filterChain.doFilter(request, response);
    }
}