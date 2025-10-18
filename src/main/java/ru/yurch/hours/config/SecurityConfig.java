package ru.yurch.hours.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String FIND_USER = """
            SELECT username, password, enabled
            FROM users
            WHERE LOWER(username) = LOWER(?)
            """;

    private static final String FIND_USERROLE = """
            SELECT u.username, a.authority
            FROM authorities a, users u
            WHERE LOWER(u.username) = LOWER(?) and u.authority_id = a.id
            """;

    private final DataSource ds;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(ds)
                .usersByUsernameQuery(FIND_USER)
                .authoritiesByUsernameQuery(FIND_USERROLE);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login", "/reg", "/index", "/css/**", "/js/**", "/umd/**", "/send-reset-password", "/reset-password").permitAll()
                .antMatchers("/items/add").access("hasRole('ROLE_USER')")
                .antMatchers("/bindlist", "/unbind").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/**")
                .hasAnyRole("ADMIN", "USER", "EMPLOYER")
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .permitAll()
                .and()
                .csrf()
                .disable();
    }

}
