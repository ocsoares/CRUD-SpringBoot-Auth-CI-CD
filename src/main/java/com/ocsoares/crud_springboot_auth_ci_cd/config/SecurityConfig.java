package com.ocsoares.crud_springboot_auth_ci_cd.config;

import com.ocsoares.crud_springboot_auth_ci_cd.security.JwtAuthFilter;
import com.ocsoares.crud_springboot_auth_ci_cd.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                   .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                   .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                       response.setContentType("application/json");
                       response.setCharacterEncoding("UTF-8");
                       response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                       response.getWriter().write("""
                               {"status":401,"error":"Unauthorized","message":"Authentication required","timestamp":"%s"}
                               """.formatted(LocalDateTime.now()));
                   }).accessDeniedHandler((request, response, accessDeniedException) -> {
                       response.setContentType("application/json");
                       response.setCharacterEncoding("UTF-8");
                       response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                       response.getWriter().write("""
                               {"status":403,"error":"Forbidden","message":"Access denied","timestamp":"%s"}
                               """.formatted(LocalDateTime.now()));
                   })).authorizeHttpRequests(
                        auth -> auth.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll().anyRequest()
                                    .authenticated())
                   .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsServiceImpl userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}