package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.AuthRequstDTO;
import com.diplom.demo.DTO.EmailDTO;
import com.diplom.demo.DTO.PasswordResetDTO;
import com.diplom.demo.DTO.RegisterRequestDTO;
import com.diplom.demo.Entity.User;
import com.diplom.demo.Service.AuthenticationService;
import com.diplom.demo.Service.PasswordResetService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;
    @Autowired
    private PasswordResetService resetService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        log.info("Регистрация пользователя: {}", registerRequestDTO);
        return ResponseEntity.ok(authService.register(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequstDTO request) {
        return ResponseEntity.ok(authService.authenticate(request.getUsername(), request.getPassword()));
    }
    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestReset(@RequestBody EmailDTO dto) {
        resetService.sendResetCode(dto.getEmail());
        return ResponseEntity.ok("Код отправлен на почту");
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<?> confirmReset(@RequestBody PasswordResetDTO dto) {
        boolean result = resetService.resetPassword(dto.getEmail(), dto.getCode(), dto.getNewPassword());
        return result ? ResponseEntity.ok("Пароль изменён") : ResponseEntity.badRequest().body("Неверный код или просрочен");
    }

}

