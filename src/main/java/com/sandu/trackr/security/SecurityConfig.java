package com.sandu.trackr.security;

import com.sandu.trackr.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/confirm-account",
                        "/api/auth/resend-verification-code",
                        "/api/auth/reset-password",
                        "/api/auth/google",
                        "/api/test"
                )
                .permitAll()
                .anyRequest()
                .authenticated();

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        ;
        return http.build();
    }

@Bean
CorsConfigurationSource corsConfigurationSource(    @Value("${security.allowed.origins}") final List<String> origins,
                                                    @Value("${security.allowed.headers}") final List<String> headers,
                                                    @Value("${security.allowed.methods}") final List<String> methods) {

    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.setAllowedOrigins(List.of("*"));
//    configuration.setAllowedOriginPatterns(List.of("*"));
//    configuration.setAllowedMethods(List.of("*"));
//    configuration.setAllowedHeaders(List.of("*"));
//    configuration.setAllowCredentials(true);
    configuration.setAllowCredentials(true);
    configuration.setAllowedOrigins(origins);
    configuration.setAllowedHeaders(headers);
    configuration.setAllowedMethods(methods);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}


}
