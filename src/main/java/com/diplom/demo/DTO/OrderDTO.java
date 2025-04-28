package com.diplom.demo.DTO;

import com.diplom.demo.Enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private Long reservationId; // ID бронирования
    private List<OrderItemDTO> items; // Список элементов заказа
}
