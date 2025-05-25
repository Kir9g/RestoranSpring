package com.diplom.demo.Service;

import com.diplom.demo.DTO.*;
import com.diplom.demo.Entity.*;
import com.diplom.demo.Enums.UserRole;
import com.diplom.demo.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    @Autowired
    private  MenuItemRepository menuItemRepository;
    @Autowired
    private  ReservationRepository reservationRepository;
    @Autowired
    private  OrderRepository orderRepository;
    @Autowired
    private  PaymentRepository paymentRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TableEntityRepository tableEntityRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    private final Path imageUploadPath = Paths.get("uploads/Menu");



    // --- Управление меню ---
    //Полное меню
    public List<MenuItemDTO> getAllMenuItems() {
        List<MenuItem> items = menuItemRepository.findAll();
        return items.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public MenuItemDTO getMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Блюдо не найдено"));

        return new MenuItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getImageUrl(),
                item.getCategory().getName(),
                item.getRestaurant().getId(), // <--- вот это добавлено
                item.isAvailable()
        );
    }


    //Меню конкретного ресторана
    public List<MenuItemDTO> getAllMenuItemByRestoranId(Long restoranId) {
        List<MenuItem> items = menuItemRepository.findByRestaurantId(restoranId);
        return items.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public MenuItemDTO addMenuItem(String name, String description, BigDecimal price,
                                   String categoryName, boolean available, MultipartFile image) throws IOException {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setAvailable(available);

        // Категория
        CategoryEntity category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new RuntimeException("Категория не найдена: " + categoryName);
        }
        item.setCategory(category);

        if (image != null && !image.isEmpty()) {
            try {
                item.setImageUrl(saveImage(image));
            }catch (Exception e){
                throw new RuntimeException("Ошибка при сохранении изображения", e);
            }
        }

        // Установка ресторана временно, если он нужен — нужно передавать restaurantId
        Restaurant restaurant = restaurantRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Нет ресторана в системе"));
        item.setRestaurant(restaurant);

        MenuItem saved = menuItemRepository.save(item);
        return convertToDTO(saved);
    }


    // Обновить элемент меню
    public MenuItemDTO updateMenuItem(Long id, String name, String description, BigDecimal price, String categoryName, boolean available, MultipartFile image) {
        MenuItem item = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Блюдо не найдено"));
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setAvailable(available);

        CategoryEntity category = categoryRepository.findByName(categoryName);
        if (category == null) throw new RuntimeException("Категория не найдена");
        item.setCategory(category);

        if (image != null && !image.isEmpty()) {
            try {
                item.setImageUrl(saveImage(image));
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при сохранении изображения", e);
            }
        }

        MenuItem saved = menuItemRepository.save(item);
        return convertToDTO(saved);
    }

    public void changeMenuItemAvailability(Long id, boolean available) {
        menuItemRepository.updateAvailabilityById(id, available);
    }

    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    // --- Бронирования ---

    public List<ReservationAdminDTO> getAllReservations() {
        return reservationRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ReservationAdminDTO> searchReservations(Long userId, String status, Boolean isExtended, LocalDateTime start, LocalDateTime end) {
        return reservationRepository.searchReservations(
                userId != null ? userId : null,
                status != null ? status : null,
                isExtended != null ? isExtended : null,
                start != null ? start : null,
                end != null ? end : null
        ).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void  cancelReservation(Long id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Броинрование не найдено"));
        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
    }

    private ReservationAdminDTO mapToDTO(Reservation reservation) {
        ReservationAdminDTO dto = new ReservationAdminDTO();
        dto.setId(reservation.getId());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setExtended(reservation.isExtended());
        dto.setStatus(reservation.getStatus());
        dto.setUserName(reservation.getUser() != null ? reservation.getUser().getUsername() : null);
        dto.setTableName(reservation.getTable() != null ? reservation.getTable().getLabel() : null); // или getId()
        return dto;
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

    public List<PaymentAdminDTO> getAllPaymentsForAdmin() {
        return paymentRepository.findAll().stream().map(payment -> {
            PaymentAdminDTO dto = new PaymentAdminDTO();
            dto.setId(payment.getId());
            dto.setPaidAt(payment.getPaidAt());
            dto.setAmount(payment.getAmount());
            dto.setStatus(payment.getStatus().toString());
            dto.setUsername(payment.getOrder().getUser().getFullName());
            dto.setMethod(payment.getMethod().toString());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<PaymentAdminDTO> searchPayments(String status, String method, String username,
                                                LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findAll().stream()
                .filter(payment -> {
                    if (status != null && !status.isEmpty()
                            && !payment.getStatus().name().equalsIgnoreCase(status)) return false;
                    if (method != null && !method.isEmpty()
                            && !payment.getMethod().equalsIgnoreCase(method)) return false;
                    if (username != null && !username.isEmpty()) {
                        if (payment.getOrder() == null || payment.getOrder().getUser() == null) return false;
                        if (!payment.getOrder().getUser().getUsername().toLowerCase().contains(username.toLowerCase()))
                            return false;
                    }
                    if (start != null && payment.getPaidAt().isBefore(start)) return false;
                    if (end != null && payment.getPaidAt().isAfter(end)) return false;
                    return true;
                })
                .map(payment -> {
                    PaymentAdminDTO dto = new PaymentAdminDTO();
                    dto.setId(payment.getId());
                    dto.setPaidAt(payment.getPaidAt());
                    dto.setAmount(payment.getAmount());
                    dto.setStatus(payment.getStatus().toString());
                    dto.setMethod(payment.getMethod());
                    dto.setUsername(payment.getOrder() != null && payment.getOrder().getUser() != null
                            ? payment.getOrder().getUser().getUsername()
                            : "N/A");
                    return dto;
                })
                .collect(Collectors.toList());
    }



    // Все бронирования
    public List<ReservationsByTimeDTO> getReservationsGroupedByTime(
            LocalDateTime date,
            Long restaurantId,
            String timeGrouping // "hour" или "day"
    ) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

        List<Reservation> reservations;

        if (restaurantId != null) {
            reservations = reservationRepository.findByRestaurantAndDateRange(
                    restaurantId,
                    startOfDay,
                    endOfDay
            );
        } else {
            reservations = reservationRepository.findByDateRange(
                    startOfDay,
                    endOfDay
            );
        }

        // Группируем по временным слотам
        Map<LocalDateTime, List<Reservation>> grouped;

        if ("hour".equals(timeGrouping)) {
            grouped = reservations.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getStartTime().withMinute(0).withSecond(0)
                    ));
        } else { // по дням
            grouped = reservations.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getStartTime().toLocalDate().atStartOfDay()
                    ));
        }

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    ReservationsByTimeDTO dto = new ReservationsByTimeDTO();
                    dto.setTimeSlot(entry.getKey());
                    dto.setReservations(entry.getValue().stream()
                            .map(this::convertToReservationDTO)
                            .collect(Collectors.toList()));
                    dto.setTotalReservations(entry.getValue().size());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ReservationDTO convertToReservationDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();

        dto.setId(reservation.getId());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setStatus(reservation.getStatus());


        // Устанавливаем tableId, если стол есть
        if (reservation.getTable() != null) {
            dto.setTableId(reservation.getTable().getId());
        }

        return dto;
    }

    // Преобразовать MenuItemDTO в MenuItem
    private MenuItem convertToEntity(MenuItemDTO itemDTO) {
        MenuItem item = new MenuItem();

        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setPrice(itemDTO.getPrice());
        item.setImageUrl(itemDTO.getImageUrl());
        item.setCategory(categoryRepository.findByName(itemDTO.getCategory()));
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
                item.getCategory().getName(),
                item.getRestaurant().getId(), // Используем только id ресторана
                item.isAvailable()
        );
    }


    //Новый код
    // ===== Комнаты =====
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public Room addRoom(RoomDTO roomDTO) {
        Restaurant restaurant = restaurantRepository.findById(roomDTO.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        Room room = new Room();
        room.setName(roomDTO.getName());
        room.setRestaurant(restaurant);

        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, RoomDTO roomDTO) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        room.setName(roomDTO.getName());

        if (roomDTO.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(roomDTO.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            room.setRestaurant(restaurant);
        }

        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    // ===== Столы =====
    public List<TableEntity> getAllTables() {
        return tableEntityRepository.findAll();
    }

    public TableEntity getTableById(Long id) {
        return tableEntityRepository.findById(id).orElseThrow(() -> new RuntimeException("Table not found"));
    }

    public TableEntity addTable(TableAdminDTO tableDTO) {
        Room room = roomRepository.findById(tableDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        TableEntity table = new TableEntity();
        table.setLabel(tableDTO.getLabel());
        table.setDescription(tableDTO.getDescription());
        table.setStringUrl(tableDTO.getImageUrl());
        table.setSeats(tableDTO.getSeats());
        table.setRoom(room);

        return tableEntityRepository.save(table);
    }

    public TableEntity updateTable(Long id, TableAdminDTO tableDTO) {
        TableEntity table = tableEntityRepository.findById(id).orElseThrow(() -> new RuntimeException("Table not found"));
        table.setLabel(tableDTO.getLabel());
        table.setDescription(tableDTO.getDescription());
        table.setStringUrl(tableDTO.getImageUrl());
        table.setSeats(tableDTO.getSeats());

        if (tableDTO.getRoomId() != null) {
            Room room = roomRepository.findById(tableDTO.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            table.setRoom(room);
        }

        return tableEntityRepository.save(table);
    }

    public void deleteTable(Long id) {
        tableEntityRepository.deleteById(id);
    }

    public List<TableEntity> getTablesByRoomId(Long roomId) {
        return tableEntityRepository.findByRoomId(roomId);
    }

    private String saveImage(MultipartFile image) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path targetPath = imageUploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/Menu/" + fileName;
    }
}

