package com.diplom.demo.Service;

import com.diplom.demo.DTO.OrderDTO;
import com.diplom.demo.DTO.OrderItemDTO;
import com.diplom.demo.Entity.Order;
import com.diplom.demo.Enums.OrderStatus;
import com.diplom.demo.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CookNotificationScheduler {

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 600000) // запускать каждые 10 минут
    public void notifyOrdersStartingSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusMinutes(29);
        LocalDateTime to = now.plusMinutes(31);

        // Найти заказы со статусом CREATED, у которых бронь начинается через ~30 минут
        List<Order> ordersToNotify = orderRepository.findOrdersWithReservationStartBetweenAndStatus(from, to, OrderStatus.CREATED);

        for (Order order : ordersToNotify) {
            messagingTemplate.convertAndSend("/topic/cooks", convertToDTO(order));
        }
    }

    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getReservation() != null ? order.getReservation().getId() : null,
                order.getItems().stream()
                        .map(item -> new OrderItemDTO(
                                item.getId(),
                                item.getComment(),
                                item.getDishName(),
                                item.getQuantity(),
                                item.getPrice()))
                        .toList()
        );
    }
}

