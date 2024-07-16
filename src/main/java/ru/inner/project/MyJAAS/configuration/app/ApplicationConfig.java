package ru.inner.project.MyJAAS.configuration.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// There are necessary components to configure spring security context
public class ApplicationConfig implements WebMvcConfigurer {

    private final UserDetailsService userDetailsService;

    @Autowired
    public ApplicationConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    // It's a crucial component that performs the actual authentication process,
    // validating a user credentials (using for this bean 'UserDetailsService') and then fulfills security context
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    // It's a key component that takes user information as request
    // in order to pass it to AuthenticationProvider(s) and then collects information about success/fail auth process
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cofing) throws Exception {
        return cofing.getAuthenticationManager();
    }

    @Bean
    // Encrypt information (in our case encode user password in order not to keep a data in a cleanliness view)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
