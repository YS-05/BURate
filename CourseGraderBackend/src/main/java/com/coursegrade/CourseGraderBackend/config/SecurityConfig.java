package com.coursegrade.CourseGraderBackend.config;

import com.coursegrade.CourseGraderBackend.security.AuthTokenFilter;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthTokenFilter authTokenFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public auth endpoints
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/verify",
                                "/api/auth/resend-verification", "/api/auth/forgot-password",
                                "/api/auth/reset-password").permitAll()

                        // Public course endpoints - anyone can browse courses
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()

                        // Reviews - GET endpoints can be public or authenticated (for isOwner flag)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/course/**").permitAll()

                        // Protected review endpoints - require authentication
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").authenticated()
                        .requestMatchers("/api/reviews/my-reviews").authenticated()

                        // Protected user endpoints
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/auth/me").authenticated()

                        // Everything else
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
