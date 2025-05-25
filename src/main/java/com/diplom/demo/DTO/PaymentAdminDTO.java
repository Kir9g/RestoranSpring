package com.diplom.demo.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentAdminDTO {
    private Long id;
    private LocalDateTime paidAt;
    private BigDecimal amount;
    private String status;
    private String method;
    private String username; // из order.user
}

