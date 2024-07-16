package ru.inner.project.MyJAAS.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import ru.inner.project.MyJAAS.configuration.AuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();

        http
                // an extra lay of security (apart from JWT token),
                // it is not necessary to protect public endpoints and ones which cannot change something (get requests)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/register", "/perform-login", "/my/logout")
                        .ignoringRequestMatchers("/api/v1/authorized/resources/free",
                                "/api/v1/authorized/resources/standard", "/api/v1/authorized/resources/premium",
                                "/api/v1/user/info", "/api/v1/user/account/update/loginandpassword",
                                "/user/account/locked", "/api/v1/admin/users/info", "/api/v1/admin/user/info/*")
                        .csrfTokenRepository(csrfTokenRepository) // a repo to storage CSRF tokens
                        .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()) // a handler for CSRF tokens
                // after successful authentication via a login form, there is created a new CSRF token for a user (by default)
                        // which is valid during of a user session
                )
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .authorizeHttpRequests()
                // accesses for all users
                .requestMatchers("/register").permitAll()
                .requestMatchers("/perform-login").permitAll()
                .requestMatchers("/my/logout").permitAll()
                // accesses for registered users (who is attended in BD)
                .requestMatchers(HttpMethod.GET, "/api/v1/authorized/resources/free").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/authorized/resources/standard").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/authorized/resources/premium").hasAnyRole("USER", "ADMIN")
                // accesses for registered users (who is attended in BD and has role 'USER')
                .requestMatchers(HttpMethod.GET, "/api/v1/user/info").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/v1/user/update").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/user/delete").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/v1/user/account/update/loginandpassword").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/user/account/perform-update").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/user/account/locked").hasRole("USER")
                // accesses for registered users (who is attended in BD and has role 'ADMIN')
                .requestMatchers(HttpMethod.GET, "/api/v1/admin/users/info").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/admin/user/info/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/admin/user/lockuseraccount").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/admin/user/unlockuseraccount").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/user/info/delete/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/admin/user/info/*/update/on/subscription").hasRole("ADMIN")
                .and()
                // in postman, it is possible to see in a tab 'Cookies' how to change JSESSIONID depend on config below
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}

