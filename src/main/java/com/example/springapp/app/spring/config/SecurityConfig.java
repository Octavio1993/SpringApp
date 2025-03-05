package com.example.springapp.app.spring.config; // Ajusta según tu estructura de paquetes

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/img/**").permitAll() // Permitir la ruta '/'
                        .anyRequest().authenticated() // Autenticar otras rutas
                )
                .formLogin(form -> form
                        .loginPage("/userForm")  // Cambiar la ruta de la página de inicio de sesión
                        .loginProcessingUrl("/login")  // Ruta para el procesamiento del login
                        .defaultSuccessUrl("/userForm") // Redirigir a /userForm después de iniciar sesión
                        .permitAll()
                );

        // Deshabilitar CSRF temporalmente para pruebas (no recomendado para producción)
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
