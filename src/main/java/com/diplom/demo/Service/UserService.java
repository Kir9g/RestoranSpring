package com.diplom.demo.Service;

import com.diplom.demo.DTO.*;
import com.diplom.demo.Entity.*;
import com.diplom.demo.Enums.OrderStatus;
import com.diplom.demo.Repository.*;
import com.diplom.demo.Service.Intergace.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public UserDTO getUserProfile(User user) {
        return mapToDto(user);
    }

    @Override
    @Transactional
    public UserDTO updateUserProfile(User user, UserDTO userDTO) {
        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        userRepository.save(user);
        return mapToDto(user);
    }

    private UserDTO mapToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
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
                        item.getCategory(),
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
    @Transactional
    public void cancelReservation(User user, Long id) {
        Reservation reservation = reservationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Reservation not found or not owned by user"));

        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
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
        return dto;
    }

    // Вспомогательные методы для работы с OrderItem
    private OrderItem convertToOrderItem(OrderItemDTO itemDto, Order order) {
        MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + itemDto.getMenuItemId()));

        OrderItem orderItem = new OrderItem();
        orderItem.setDishName(itemDto.getName());
        orderItem.setQuantity(itemDto.getQuantity());
        orderItem.setPrice(itemDto.getPrice());
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
        return dto;
    }

}
