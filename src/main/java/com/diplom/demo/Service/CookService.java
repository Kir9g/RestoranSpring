package com.diplom.demo.Service;

import com.diplom.demo.DTO.OrderDTO;
import com.diplom.demo.DTO.OrderItemDTO;
import com.diplom.demo.Entity.Order;
import com.diplom.demo.Enums.OrderStatus;
import com.diplom.demo.Repository.OrderRepository;
import com.diplom.demo.Service.Intergace.CookServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CookService implements CookServiceInterface {
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<OrderDTO> getActiveOrders() {
        List<Order> orders = orderRepository.findByStatusIn(
                List.of(OrderStatus.CREATED,
                        OrderStatus.IN_PROGRESS));
        return orders.stream().map(
                order -> new OrderDTO(
                        order.getId(),
                        order.getCreatedAt(),
                        order.getStatus(),
                        order.getReservation() != null ? order.getReservation().getId() : null,
                        order.getItems().stream()
                                .map(item -> new OrderItemDTO(item.getId(), item.getDishName(), item.getQuantity(), item.getPrice()))
                                .toList()
                )
        ).toList();
    }

    @Override
    public void acceptOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Order cannot be accepted");
        }
        order.setStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(order);
    }

    @Override
    public void completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new RuntimeException("Order is not in progress");
        }
        order.setStatus(OrderStatus.READY);
        orderRepository.save(order);
    }
}
