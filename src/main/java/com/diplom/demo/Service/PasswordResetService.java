package com.diplom.demo.Service;

import com.diplom.demo.Entity.User;
import com.diplom.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    @Autowired
    private  UserRepository userRepo;
    @Autowired
    private  JavaMailSender mailSender;
    @Autowired
    private  PasswordEncoder passwordEncoder;

    private final Map<String, ResetCodeData> resetCodes = new ConcurrentHashMap<>();

    public void sendResetCode(String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) throw new RuntimeException("Пользователь не найден");

        String code = String.format("%04d", new Random().nextInt(10000));
        resetCodes.put(email, new ResetCodeData(code, LocalDateTime.now().plusMinutes(10)));
        log.info(code);
        // Отправка email
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Код для восстановления пароля");
        message.setText("Ваш код: " + code);
        //mailSender.send(message);
    }

    public boolean resetPassword(String email, String code, String newPassword) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        ResetCodeData data = resetCodes.get(email);
        if (data == null || data.isExpired() || !data.getCode().equals(code)) return false;

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        resetCodes.remove(email); // удалить после использования
        return true;
    }
}

