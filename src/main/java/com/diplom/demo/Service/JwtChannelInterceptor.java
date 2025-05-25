package com.diplom.demo.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                String username = jwtService.extractUsername(jwt);
                if (username != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        String role = jwtService.extractClaim(jwt, claims -> claims.get("role", String.class));
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                        accessor.setUser(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("Пользователь {} подключен к WebSocket с ролью {}", username, role);
                    }
                }
            }
        }
        return message;
    }
}