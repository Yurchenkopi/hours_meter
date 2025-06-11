package ru.yurch.hours.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.yurch.hours.model.User;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@Order(2)
public class SessionFilter extends HttpFilter {

    private static final Logger LOG = LoggerFactory.getLogger(SessionFilter.class.getName());

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var session = request.getSession();
        addUserToSession(session, request);
        chain.doFilter(request, response);
    }

    private void addUserToSession(HttpSession session, HttpServletRequest request) {
        var user = (User) session.getAttribute("user");
        LOG.info("Current user is {}", user);
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        request.setAttribute("user", user);
    }
}
