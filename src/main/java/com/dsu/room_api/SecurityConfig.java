package com.dsu.room_api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ApiAuthenticationEntryPoint entryPoint) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup",
                                "/api/auth/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-ui.html", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(b -> b.authenticationEntryPoint(entryPoint))
                .oauth2ResourceServer(o -> o
                        .authenticationEntryPoint(entryPoint)
                        .jwt(Customizer.withDefaults()))
                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint));
        return http.build();
    }
}