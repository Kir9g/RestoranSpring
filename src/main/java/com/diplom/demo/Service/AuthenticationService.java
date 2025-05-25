package com.diplom.demo.Service;

import com.diplom.demo.DTO.RegisterRequestDTO;
import com.diplom.demo.Entity.User;
import com.diplom.demo.Enums.UserRole;
import com.diplom.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@Slf4j
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public String register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()){
            throw new RuntimeException("Пользователь уже существует");
        }
        User user = new User();

        user.setUsername(registerRequestDTO.getUsername());

        //Заполнение зашифрованного пароля
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRole(UserRole.CLIENT);


        if (registerRequestDTO.getEmail() != null && !registerRequestDTO.getEmail().isEmpty()) {
            user.setEmail(registerRequestDTO.getEmail());
        } else {
            user.setEmail(""); // или null, или любое значение по умолчанию
        }

        if (registerRequestDTO.getFullName() != null && !registerRequestDTO.getFullName().isEmpty()) {
            user.setFullName(registerRequestDTO.getFullName());
        }

        if (registerRequestDTO.getPhone() != null && !registerRequestDTO.getPhone().isEmpty()) {
            user.setPhone(registerRequestDTO.getPhone());
        }
        userRepository.save(user);
        return jwtService.generateToken(user);
    }
    @Transactional
    public String authenticate(String username, String password) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        User user = (User) authentication.getPrincipal();
        return jwtService.generateToken(user);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }
}

