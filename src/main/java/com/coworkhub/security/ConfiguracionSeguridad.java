package com.coworkhub.security;

import com.coworkhub.persistence.repository.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Módulo 08. Primero se probó con Basic Auth (UserDetailsService + BCrypt); la versión final es JWT stateless:
 * sin sesión, sin CSRF y cada petición trae su token.
 * Los permisos por rol van con @PreAuthorize en los controladores (@EnableMethodSecurity)
 * y la regla de "dueño" (un MEMBER solo lo suyo) se valida en la capa de negocio con UsuarioActual.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfiguracionSeguridad {

    /** Rutas públicas: login, salud, Swagger y el frontend (archivos estáticos). */
    private static final String[] RUTAS_PUBLICAS = {
            "/auth/login", "/actuator/health", "/actuator/health/**",
            "/doc/**", "/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/error",
            "/", "/index.html", "/*.html", "/css/**", "/js/**", "/img/**", "/favicon.ico"};

    @Bean
    public SecurityFilterChain cadenaSeguridad(HttpSecurity http, JwtServicio jwtServicio,
                                               RespuestaErrorSeguridad respuestaError) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(RUTAS_PUBLICAS).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(respuestaError)   // 401
                        .accessDeniedHandler(respuestaError))       // 403
                .addFilterBefore(new JwtFiltro(jwtServicio), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** Carga usuarios desde la tabla user_accounts. Es lo que usaba la fase de Basic Auth. */
    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository repositorio) {
        return nombre -> repositorio.findByUsername(nombre)
                .map(c -> User.withUsername(c.getUsername())
                        .password(c.getPassword())
                        .roles(c.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(nombre));
    }
}
