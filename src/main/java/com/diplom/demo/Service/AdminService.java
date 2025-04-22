package com.diplom.demo.Service;

import com.diplom.demo.DTO.MenuItemDTO;
import com.diplom.demo.DTO.UserDTO;
import com.diplom.demo.Entity.*;
import com.diplom.demo.Enums.UserRole;
import com.diplom.demo.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    @Autowired
    private final MenuItemRepository menuItemRepository;
    @Autowired
    private final ReservationRepository reservationRepository;
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final PaymentRepository paymentRepository;
    @Autowired
    private final UserRepository userRepository;

    // --- Управление меню ---
    //Полное меню
    public List<MenuItemDTO> getAllMenuItems() {
        List<MenuItem> items = menuItemRepository.findAll();
        return items.stream()
                .map(this::convertToDTO)
                .toList();
    }

    //Меню конкретного ресторана
    public List<MenuItemDTO> getAllMenuItemByRestoranId(Long restoranId) {
        List<MenuItem> items = menuItemRepository.findByRestaurantId(restoranId);
        return items.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public MenuItemDTO addMenuItem(MenuItemDTO itemDTO) {
        MenuItem item = convertToEntity(itemDTO);
        MenuItem savedItem = menuItemRepository.save(item);
        return convertToDTO(savedItem);
    }

    // Обновить элемент меню
    public Optional<MenuItemDTO> updateMenuItem(Long id, MenuItemDTO updatedItemDTO) {
        return menuItemRepository.findById(id).map(item -> {
            item.setName(updatedItemDTO.getName());
            item.setDescription(updatedItemDTO.getDescription());
            item.setPrice(updatedItemDTO.getPrice());
            item.setCategory(updatedItemDTO.getCategory());
            item.setImageUrl(updatedItemDTO.getImageUrl());
            item.setAvailable(updatedItemDTO.isAvailable());
            MenuItem savedItem = menuItemRepository.save(item);
            return convertToDTO(savedItem);
        });
    }

    public void changeMenuItemAvailability(Long id, boolean available) {
        menuItemRepository.updateAvailabilityById(id, available);
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    // --- Бронирования ---

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> searchReservations(Long userId, String status, Boolean isExtended, LocalDateTime start, LocalDateTime end) {
        return reservationRepository.searchReservations(
                userId != null ? userId : null,
                status != null ? status : null,
                isExtended != null ? isExtended : null,
                start != null ? start : null,
                end != null ? end : null
        );
    }


    // --- Заказы ---

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // --- Платежи ---

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public UserDTO changeUserRole(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Здесь мы просто изменяем роль пользователя, остальные поля остаются без изменений
        user.setRole(userDTO.getRole());

        // Сохраняем изменения в базе данных
        userRepository.save(user);

        return userDTO;
    }

    // Получить сумму выручки за выбранный период
    public BigDecimal getRevenueForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> payments = paymentRepository.findAllByPaidAtBetween(startDate, endDate);

        // Суммируем все платежи за указанный период
        return payments.stream()
                .map(Payment::getAmount) // предполагаем, что в Payment есть поле amount
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // Преобразовать MenuItemDTO в MenuItem
    private MenuItem convertToEntity(MenuItemDTO itemDTO) {
        MenuItem item = new MenuItem();
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setPrice(itemDTO.getPrice());
        item.setImageUrl(itemDTO.getImageUrl());
        item.setCategory(itemDTO.getCategory());
        item.setAvailable(itemDTO.isAvailable());
        return item;
    }

    // Преобразовать MenuItem в MenuItemDTO
    private MenuItemDTO convertToDTO(MenuItem item) {
        return new MenuItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getImageUrl(),
                item.getCategory(),
                item.getRestaurant().getId(), // Используем только id ресторана
                item.isAvailable()
        );
    }
}

