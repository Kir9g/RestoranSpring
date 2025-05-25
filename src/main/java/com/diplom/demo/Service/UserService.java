package com.diplom.demo.Service;

import com.diplom.demo.DTO.TableStatusRequestDTO;
import com.diplom.demo.DTO.*;
import com.diplom.demo.Entity.*;
import com.diplom.demo.Enums.OrderStatus;
import com.diplom.demo.Enums.PaymentStatus;
import com.diplom.demo.Repository.*;
import com.diplom.demo.Service.Intergace.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class UserService implements UserServiceInterface {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private TableEntityRepository tableEntityRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public UserDTO getUserProfile(User user) {
        return mapToDto(user);
    }

    @Override
    @Transactional
    public UserDTO updateUserProfile(User user, UserDTO userDTO) {
        user.setFullName(userDTO.getFullName());
        user.setSecondName(userDTO.getSecondName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        userRepository.save(user);
        return mapToDto(user);
    }

    private UserDTO mapToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setSecondName(user.getSecondName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }


    @Override
    public List<MenuItemDTO> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(item -> new MenuItemDTO(item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getImageUrl(),
                        item.getCategory().getName(),
                        item.getRestaurant().getId(),
                        item.isAvailable()))
                .toList();
    }

    @Override
    @Transactional
    public OrderDTO createOrder(User user, OrderDTO orderDTO) {
        // Создаем основной заказ
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setUser(user);

        // Устанавливаем бронирование, если указано
        if (orderDTO.getReservationId() != null) {
            Reservation reservation = reservationRepository.findById(orderDTO.getReservationId())
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));
            order.setReservation(reservation);
            reservation.addOrder(order);
        }

        // Сохраняем заказ, чтобы получить ID
        Order savedOrder = orderRepository.save(order);


        // Добавляем элементы заказа
        if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            List<OrderItem> orderItems = orderDTO.getItems().stream()
                    .map(itemDto -> convertToOrderItem(itemDto, savedOrder))
                    .collect(Collectors.toList());

            savedOrder.setItems(orderItems);
            orderRepository.save(savedOrder); // Сохраняем с элементами
        }

        return convertToOrderDTO(savedOrder);
    }


    @Override
    public List<OrderDTO> getMyOrders(User user) {
        return orderRepository.findByUser(user).stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationDTO createReservation(User user, ReservationDTO reservationDTO) {
        TableEntity table = tableEntityRepository.findById(reservationDTO.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));


        Reservation reservation = new Reservation();
        reservation.setStartTime(reservationDTO.getStartTime());
        reservation.setEndTime(reservationDTO.getEndTime());
        reservation.setStatus("ACTIVE");
        reservation.setUser(user);
        reservation.setTable(table);

        Reservation savedReservation = reservationRepository.save(reservation);
        return convertToReservationDTO(savedReservation);
    }

    @Override
    public List<ReservationDTO> getMyReservations(User user) {
        return reservationRepository.findByUser(user).stream()
                .map(this::convertToReservationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTOLIST> getMyActiveReservations(User user) {
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findActiveReservationsNow(user, now).stream()
                .map(this::convertToReservationDTOLIST)
                .collect(Collectors.toList());
    }
    @Override
    public List<ReservationDTOLIST> getMyPastReservations(User user) {
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findPastReservations(user, now).stream()
                .map(this::convertToReservationDTOLIST)
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public void cancelReservation(User user, Long id) {
        Reservation reservation = reservationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Reservation not found or not owned by user"));

        LocalDateTime now = LocalDateTime.now();
        if (reservation.getStartTime().minusHours(2).isBefore(now)) {
            throw new RuntimeException("Cannot cancel reservation less than 2 hours before start");
        }

        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
    }

    public List<TableStatusDTO> getAllTablesWithStatus(TableStatusRequestDTO request) {
        List<TableEntity> tables = tableEntityRepository.findAll();
        LocalDateTime checkTime = request.getCheckTime() != null
                ? request.getCheckTime()
                : LocalDateTime.now();

        return tables.stream().map(table -> {
            TableStatusDTO dto = new TableStatusDTO();
            dto.setTableId(table.getId());
            dto.setTableName(table.getLabel());
            dto.setCapacity(table.getSeats());
            dto.setRoomName(table.getRoom().getName());

            // Проверяем активные бронирования для этого стола
            List<Reservation> activeReservations = reservationRepository
                    .findByTableAndTimeRange(
                            table,
                            checkTime
                    );

            dto.setOccupied(!activeReservations.isEmpty());
            if (!activeReservations.isEmpty()) {
                dto.setOccupiedUntil(activeReservations.get(0).getEndTime());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    //оплата
    @Transactional
    public PaymentDTO mockPay(User user, Long orderId, String method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: not your order");
        }

        if (paymentRepository.existsByOrderId(orderId)) {
            throw new RuntimeException("This order is already paid");
        }

        if (order.getReservation() == null || !order.getReservation().getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Order must be linked to an ACTIVE reservation");
        }

        if ("WAITER".equalsIgnoreCase(method)) {
            notificationService.notifyWaiters("Клиент вызвал официанта для оплаты. Заказ ID: " + orderId);
            return null; // Или можно вернуть DTO с сообщением
        }

        order.setStatus(OrderStatus.PAID);

        // Рассчёт суммы
        BigDecimal totalAmount = order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        // Создание и сохранение оплаты
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setPaidAt(LocalDateTime.now());
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.COMPLETED);

        paymentRepository.save(payment);

        return PaymentDTO.fromPayment(payment);
    }

    public OrderPaymentDTO getOrderPaymentDTO(User user, Long orderId) {
        Order order = orderRepository.findById( orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден"));

        boolean paid = order.getStatus() == OrderStatus.PAID;

        List<ItemDTO> items = order.getItems().stream()
                .map(item -> new ItemDTO(
                        item.getMenuItem().getId(),
                        item.getMenuItem().getName(),
                        item.getQuantity(),
                        item.getMenuItem().getPrice()
                ))
                .toList();

        return new OrderPaymentDTO(order.getId(), order.getReservation().getId(), items, paid);
    }

    public OrderDTO getOrderById(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (!order.getReservation().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Вы не можете просматривать этот заказ");
        }

        return convertToOrderDTO(order);
    }





    //Доп функции

    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatus(order.getStatus());
        if (order.getReservation() != null) {
            dto.setReservationId(order.getReservation().getId());
        }

        // ✅ Добавим items
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(this::convertToOrderItemDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setItems(new ArrayList<>());
        }

        return dto;
    }


    // Вспомогательные методы для работы с OrderItem
    private OrderItem convertToOrderItem(OrderItemDTO itemDto, Order order) {
        MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + itemDto.getMenuItemId()));

        OrderItem orderItem = new OrderItem();
        orderItem.setDishName(menuItem.getName());
        orderItem.setComment(itemDto.getComment());
        orderItem.setQuantity(itemDto.getQuantity());
        orderItem.setPrice(menuItem.getPrice());
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem); // если нужно сохранить связь с оригинальным MenuItem

        return orderItem;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setMenuItemId(orderItem.getMenuItem() != null ? orderItem.getMenuItem().getId() : null);
        dto.setName(orderItem.getDishName());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
    }

    private ReservationDTO convertToReservationDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setStatus(reservation.getStatus());
        dto.setTableId(reservation.getTable().getId());


        // Проверяем, можно ли отменить бронирование
        LocalDateTime now = LocalDateTime.now();
        dto.setCanBeCancelled("ACTIVE".equals(reservation.getStatus()) &&
                reservation.getStartTime().minusHours(2).isAfter(now));
        return dto;
    }

    private ReservationDTOLIST convertToReservationDTOLIST(Reservation reservation) {
        ReservationDTOLIST dto = new ReservationDTOLIST();
        dto.setId(reservation.getId());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setStatus(reservation.getStatus());
        dto.setTableId(reservation.getTable().getId());
        dto.setLabelName(reservation.getTable().getLabel());
        dto.setRoomName(reservation.getTable().getRoom().getName());
        if (reservation.getOrders() != null) {
            dto.setOrderIds(reservation.getOrders().stream()
                    .map(Order::getId)
                    .collect(Collectors.toList()));
        }


        // Проверяем, можно ли отменить бронирование
        LocalDateTime now = LocalDateTime.now();
        dto.setCanBeCancelled("ACTIVE".equals(reservation.getStatus()) &&
                reservation.getStartTime().minusHours(2).isAfter(now));

        return dto;
    }


}
