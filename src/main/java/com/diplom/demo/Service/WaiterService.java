package com.diplom.demo.Service;

import com.diplom.demo.DTO.OrderDTO;
import com.diplom.demo.DTO.OrderItemDTO;
import com.diplom.demo.DTO.ReservationDTO;
import com.diplom.demo.Entity.Order;
import com.diplom.demo.Entity.Reservation;
import com.diplom.demo.Enums.OrderStatus;
import com.diplom.demo.Repository.OrderRepository;
import com.diplom.demo.Repository.ReservationRepository;
import com.diplom.demo.Service.Intergace.WaiterServiceInterface;
import com.diplom.demo.Service.WaiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WaiterService implements WaiterServiceInterface {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<ReservationDTO> getAllActiveReservations() {
        return reservationRepository.findByStatus("ACTIVE")
                .stream()
                .map(reservation -> new ReservationDTO(
                        reservation.getId(),
                        reservation.getUser().getId(),
                        reservation.getTable().getId(),
                        reservation.getStartTime(),
                        reservation.getEndTime(),
                        reservation.getStatus(),
                        reservation.isExtended()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));
        reservation.setStatus("CONFIRMED");
        reservationRepository.save(reservation);

        return new ReservationDTO(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getTable().getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus(),
                reservation.isExtended()
        );
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderDTO(
                        order.getId(),
                        order.getCreatedAt(),
                        order.getStatus(),
                        order.getReservation() != null ? order.getReservation().getId() : null,
                        order.getItems().stream()
                                .map(item -> new OrderItemDTO(
                                        item.getId(),
                                        item.getDishName(),
                                        item.getQuantity(),
                                        item.getPrice()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        order.setStatus(OrderStatus.valueOf(status));
        orderRepository.save(order);

        return new OrderDTO(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getReservation() != null ? order.getReservation().getId() : null,
                order.getItems().stream()
                        .map(item -> new OrderItemDTO(
                                item.getId(),
                                item.getDishName(),
                                item.getQuantity(),
                                item.getPrice()
                        ))
                        .collect(Collectors.toList())
        );
    }
}

