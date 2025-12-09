package com.example.helpdesk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * Spring Security configuration for session-based authentication.
 * 
 * @author Facility Helpdesk Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configure security filter chain with session-based authentication.
     * 
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Allow access to login pages, root path, and static resources
                .requestMatchers("/", "/login", "/admin/login", "/css/**", "/js/**", "/images/**").permitAll()
                // Admin-only routes - only ADMIN can access
                .requestMatchers("/admin/users/**").hasRole("ADMIN")
                // Admin and Staff routes - both ADMIN and STAFF can access
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "STAFF")
                // User routes - only STUDENT can access
                .requestMatchers("/tickets/**").hasRole("STUDENT")
                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"))
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
                .invalidSessionUrl("/login?expired")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login?expired")
            )
            .csrf(csrf -> csrf.disable()); // Disable CSRF for development (enable in production)

        return http.build();
    }

    /**
     * Custom authentication success handler bean.
     * Uses CustomAuthenticationSuccessHandler component.
     */
    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    /**
     * Password encoder bean for encoding and verifying passwords.
     * Using NoOpPasswordEncoder for development (no encryption).
     * WARNING: This is NOT secure for production! Use BCryptPasswordEncoder in production.
     * 
     * @return NoOpPasswordEncoder instance (no encryption)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}

