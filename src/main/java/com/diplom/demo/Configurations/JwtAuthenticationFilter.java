package com.diplom.demo.Configurations;

import com.diplom.demo.Service.CustomUserDetailsService;
import com.diplom.demo.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;  // Сервис для работы с JWT
    @Autowired
    private CustomUserDetailsService userDetailsService; // Сервис для загрузки данных пользователя

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Извлекаем заголовок "Authorization" (формат: "Bearer <token>")
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Если заголовок не существует или не в формате "Bearer <token>", продолжаем обработку запроса
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Извлекаем сам токен
        jwt = authHeader.substring(7); // Убираем "Bearer " из начала строки
        username = jwtService.extractUsername(jwt);  // Извлекаем имя пользователя из токена

        // Проверяем, есть ли токен и аутентификация не установлена
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Загружаем пользователя с помощью UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Проверяем валидность токена
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Если токен валидный, создаём объект аутентификации
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Устанавливаем аутентификацию в контекст безопасности
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Переходим к следующему фильтру
        filterChain.doFilter(request, response);
    }
}


