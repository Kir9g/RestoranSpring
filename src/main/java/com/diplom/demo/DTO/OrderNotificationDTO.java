package com.diplom.demo.DTO;

import com.diplom.demo.Enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
public class OrderNotificationDTO {
    private Long orderId;
    private LocalDateTime createdAt;
    private OrderStatus status;

}
