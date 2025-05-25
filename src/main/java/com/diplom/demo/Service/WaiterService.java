package com.diplom.demo.Service;

import com.diplom.demo.DTO.*;
import com.diplom.demo.Entity.*;
import com.diplom.demo.Enums.OrderStatus;
import com.diplom.demo.Enums.PaymentStatus;
import com.diplom.demo.Repository.OrderRepository;
import com.diplom.demo.Repository.PaymentRepository;
import com.diplom.demo.Repository.ReservationRepository;
import com.diplom.demo.Repository.TableEntityRepository;
import com.diplom.demo.Service.Intergace.WaiterServiceInterface;
import com.diplom.demo.Service.WaiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WaiterService implements WaiterServiceInterface {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private TableEntityRepository tableRepository;
    @Autowired
    private NotificationService notificationService;


    @Override
    public List<ReservationDTO> getAllActiveReservations() {
        return reservationRepository.findByStatus("ACTIVE")
                .stream()
                .map(reservation  -> {
                    ReservationDTO dto = new ReservationDTO();
                    dto.setId(reservation.getId());
                    dto.setStartTime(reservation.getStartTime());
                    dto.setEndTime(reservation.getEndTime());
                    dto.setStatus(reservation.getStatus());
                    dto.setTableId(reservation.getTable().getId());
                    dto.setTableLabel(reservation.getTable().getLabel());
                    dto.setRoomName(reservation.getTable().getRoom().getName());

                    LocalDateTime now = LocalDateTime.now();
                    dto.setCanBeCancelled("ACTIVE".equals(reservation.getStatus()) &&
                            reservation.getStartTime().minusHours(2).isAfter(now));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));
        reservation.setStatus("CONFIRMED");
        reservationRepository.save(reservation);

        return convertToReservationDTO(reservation);
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
                                        item.getComment(),
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
                                item.getComment(),
                                item.getDishName(),
                                item.getQuantity(),
                                item.getPrice()
                        ))
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public PaymentDTO confirmPayment(Long orderId, String method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (paymentRepository.existsByOrderId(orderId)) {
            throw new RuntimeException("This order is already paid");
        }

        if (!method.equalsIgnoreCase("CASH") && !method.equalsIgnoreCase("CARD")) {
            throw new IllegalArgumentException("Invalid payment method. Use 'CARD' or 'CASH'.");
        }

        BigDecimal totalAmount = order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);



        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setPaidAt(LocalDateTime.now());
        payment.setMethod(method.toUpperCase());
        payment.setStatus(PaymentStatus.COMPLETED);

        paymentRepository.save(payment);

        if (order.getReservation() != null) {
            TableEntity table = order.getReservation().getTable();
            if (table.isManuallyOccupied()) {
                table.setManuallyOccupied(false);
                tableRepository.save(table);
            }
        }

        return PaymentDTO.fromPayment(payment);
    }
    public void freeTable(Long tableId) {
        TableEntity table = tableRepository.findById(tableId).orElseThrow();
        table.setManuallyOccupied(false);
        tableRepository.save(table);
    }

    @Transactional
    public OrderDTO createManualOrderWithItems(CreateOrderRequest request, User waiter) {
        TableEntity table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("Стол не найден"));

        if (!table.isManuallyOccupied()) {
            throw new IllegalStateException("Стол не занят вручную");
        }
        Reservation reservation = new Reservation();
        reservation.setTable(table);
        reservation.setStartTime(LocalDateTime.now());
        reservation.setEndTime(LocalDateTime.now().plusHours(2)); // например на 2 часа
        reservation.setStatus("ACTIVE"); // статус для ручных бронирований
        reservation.setUser(waiter);
        reservationRepository.save(reservation);

        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setReservation(reservation);
        order.setUser(waiter); // Не привязываем к пользователю

        tableRepository.save(table);

        // Сохраняем сначала заказ, чтобы получить ID
        orderRepository.save(order);

        List<OrderItem> items = request.getItems().stream().map(dto -> {
            OrderItem item = new OrderItem();
            item.setDishName(dto.getName());
            item.setQuantity(dto.getQuantity());
            item.setPrice(dto.getPrice());
            item.setComment(dto.getComment());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);
        orderRepository.save(order); // сохраняем заказ ещё раз с добавленными блюдами

        notificationService.notifyCooks(new OrderNotificationDTO(order.getId(), order.getCreatedAt(), order.getStatus()).toString());


        return new OrderDTO(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                null,
                items.stream().map(i -> new OrderItemDTO(
                        i.getId(),
                        i.getComment(),
                        i.getDishName(),
                        i.getQuantity(),
                        i.getPrice()
                )).collect(Collectors.toList())
        );
    }



    private ReservationDTO convertToReservationDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setStatus(reservation.getStatus());
        dto.setTableId(reservation.getTable().getId());
        dto.setTableLabel(reservation.getTable().getLabel());
        dto.setRoomName(reservation.getTable().getRoom().getName());

        // Проверяем, можно ли отменить бронирование
        LocalDateTime now = LocalDateTime.now();
        dto.setCanBeCancelled("ACTIVE".equals(reservation.getStatus()) &&
                reservation.getStartTime().minusHours(2).isAfter(now));
        return dto;
    }
}

