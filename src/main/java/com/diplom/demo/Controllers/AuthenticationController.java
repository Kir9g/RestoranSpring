package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.AuthRequstDTO;
import com.diplom.demo.DTO.RegisterRequestDTO;
import com.diplom.demo.Entity.User;
import com.diplom.demo.Service.AuthenticationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.ok(authService.register(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequstDTO request) {
        return ResponseEntity.ok(authService.authenticate(request.getUsername(), request.getPassword()));
    }
}

