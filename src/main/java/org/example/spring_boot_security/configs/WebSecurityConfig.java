package org.example.spring_boot_security.configs;

import lombok.AllArgsConstructor;
import org.example.spring_boot_security.service.UserService;
import org.example.spring_boot_security.util.JwtAuthFilter;
import org.example.spring_boot_security.util.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   JwtAuthFilter jwtAuthFilter) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)// Отключаем CSRF защиту для REST API
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Включаем CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/user").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/admin/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .logout(lOut -> lOut // Настройка выхода из системы
                        .invalidateHttpSession(true) // Очистка сессии
                        .clearAuthentication(true) // Очистка аутентификации
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL для выхода
                        .permitAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Без сессий (STATELESS), каждый запрос аутентифицируется отдельно
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Добавляем JWT фильтр

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // Бин для аутентификации
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Кодировщик паролей
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtUtils jwtUtils, UserService userService) {
        return new JwtAuthFilter(jwtUtils, userService); // Создаём JWT фильтр
    }

    //защищает от межсайтовых запросов
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:5500",  // Live Server
                "http://127.0.0.1:5500",  // Альтернативный адрес
                "http://localhost:8080",  // Сервер разработки
                "*" // или разрешены все домены
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS" // Разрешённые HTTP методы
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Cache-Control", "Content-Type" // Разрешённые заголовки
        ));
        configuration.setAllowCredentials(true); // Разрешаем куки и заголовки авторизации
        configuration.setMaxAge(3600L); // Кешировать настройки CORS 1 час

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Применяем ко всем путям
        return source;
    }
}
