package com.diplom.demo.Service;

import com.diplom.demo.DTO.ReservationCodeDTO;
import com.diplom.demo.Entity.Reservation;
import com.diplom.demo.Entity.TableEntity;
import com.diplom.demo.Entity.User;
import com.diplom.demo.Repository.ReservationRepository;
import com.diplom.demo.Repository.TableEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private TableEntityRepository tableEntityRepository;

    private final Map<String, String> codeStorage = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Transactional
    public void generateAndSendCode(User user, ReservationCodeDTO dto) {
        String code = String.format("%04d", new Random().nextInt(10000));
        codeStorage.put(user.getEmail(), code);

        TableEntity table = tableEntityRepository.findById(dto.getTableid())
                .orElseThrow(() -> new RuntimeException("Table not found"));
        boolean isTaken = reservationRepository.existsByTableIdAndTimeOverlap(
                dto.getTableid(), dto.getStartTime(), dto.getEndTime()
        );
        if (isTaken) {
            throw new RuntimeException("Столик уже забронирован на это время");
        }


        Reservation reservation = new Reservation();
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setStatus("PENDING");
        reservation.setUser(user);
        reservation.setCreatedTime(LocalDateTime.now());
        reservation.setTable(table);

        Reservation savedReservation = reservationRepository.save(reservation);
        shed(savedReservation,user);

        // Здесь можно вставить реальную отправку email
        System.out.println("Отправка кода на почту " + user.getEmail() + ": " + code);

    }
    @Transactional
    public void verifyAndActivateReservation(User user, ReservationCodeDTO dto) {
        if (!verifyCode(user.getEmail(), dto.getCode())) {
            throw new RuntimeException("неверный код");
        }
        Reservation pending = reservationRepository.findByUserAndTableIdAndStatus(
                user, dto.getTableid(), "PENDING"
        ).orElseThrow(() -> new RuntimeException("Временная бронь не найдена"));


        pending.setStatus("ACTIVE");
        reservationRepository.save(pending);
        invalidateCode(user.getEmail());

    }

    public boolean verifyCode(String email, String code) {
        return code.equals(codeStorage.get(email));
    }

    public void invalidateCode(String email) {
        codeStorage.remove(email);
    }

    public void shed(Reservation reservation, User user){
        scheduler.schedule(() -> {
            try {
                Reservation pending = reservationRepository.findById(reservation.getId()).orElse(null);
                if (pending != null && "PENDING".equals(pending.getStatus())) {
                    reservationRepository.delete(pending);
                    codeStorage.remove(user.getEmail());
                    System.out.println("Удалена неподтвержденная бронь для " + user.getEmail());
                }
            } catch (Exception e) {
                System.err.println("Ошибка при удалении неподтвержденной брони: " + e.getMessage());
            }
        }, 3, TimeUnit.MINUTES);
    }
}
