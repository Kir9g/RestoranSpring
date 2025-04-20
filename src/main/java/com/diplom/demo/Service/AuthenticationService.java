package com.diplom.demo.Service;

import com.diplom.demo.DTO.RegisterRequestDTO;
import com.diplom.demo.Entity.User;
import com.diplom.demo.Enums.UserRole;
import com.diplom.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public String register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()){
            throw new RuntimeException("Пользователь уже существует");
        }
        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());

        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRole(UserRole.CLIENT);
        userRepository.save(user);
        return jwtService.generateToken(user);
    }

    public String authenticate(String username, String password) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        User user = (User) authentication.getPrincipal();
        return jwtService.generateToken(user);
    }

}

