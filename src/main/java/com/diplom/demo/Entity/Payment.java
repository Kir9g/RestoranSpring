package com.diplom.demo.Entity;

import com.diplom.demo.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime paidAt;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, COMPLETED, FAILED

    private String method; // CARD, CASH, ONLINE

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}

