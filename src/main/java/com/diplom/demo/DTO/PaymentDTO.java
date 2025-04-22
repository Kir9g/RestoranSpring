package com.diplom.demo.DTO;


import com.diplom.demo.Entity.Order;
import com.diplom.demo.Entity.Payment;
import com.diplom.demo.Enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {

    private Long id;
    private LocalDateTime paidAt;
    private BigDecimal amount;
    private PaymentStatus status; // PENDING, COMPLETED, FAILED
    private String method; // CARD, CASH, ONLINE
    private Long orderId; // ID заказа, связанного с этим платежом

    // Метод для преобразования Payment в PaymentDTO
    public static PaymentDTO fromPayment(Payment payment) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setId(payment.getId());
        paymentDTO.setPaidAt(payment.getPaidAt());
        paymentDTO.setAmount(payment.getAmount());
        paymentDTO.setStatus(payment.getStatus());
        paymentDTO.setMethod(payment.getMethod());
        paymentDTO.setOrderId(payment.getOrder().getId()); // Получаем ID заказа
        return paymentDTO;
    }
}

