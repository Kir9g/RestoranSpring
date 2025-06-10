package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.*;
import com.diplom.demo.Entity.*;
import com.diplom.demo.Enums.UserRole;
import com.diplom.demo.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Только для админа
public class AdminController {

    @Autowired
    private final AdminService adminService;

    // ----- Меню -----
    //Добавить
    @PostMapping(value = "/menu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')") // Только для админа
    public ResponseEntity<MenuItemDTO> addMenuItem(
            @RequestPart("name") String name,
            @RequestPart("description") String description,
            @RequestPart("price") BigDecimal price,
            @RequestPart("category") String category,
            @RequestPart("available") boolean available,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok(adminService.addMenuItem(name, description, price, category, available, image));
    }
    //Обновить
    @PutMapping(value = "/menu/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemDTO> updateMenuItemMultipart(
            @PathVariable Long id,
            @RequestPart("name") String name,
            @RequestPart("description") String description,
            @RequestPart("price") BigDecimal price,
            @RequestPart("category") String category,
            @RequestPart("available") boolean available,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(adminService.updateMenuItem(id, name, description, price, category, available, image));
    }
    // удалить
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/menu/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        adminService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
    //Вывести все меню
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/menu")
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        return ResponseEntity.ok(adminService.getAllMenuItems());
    }

    //Вевести меню конкретного ресторана
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("Menu/{id}")
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItemsByRestoranId(@PathVariable Long id){
        return ResponseEntity.ok(adminService.getAllMenuItemByRestoranId(id));
    }

    @GetMapping("/menu/item/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getMenuItemById(id));
    }


    @PutMapping("/menu/{id}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeMenuItemAvailability(@PathVariable Long id, @RequestParam boolean available) {
        adminService.changeMenuItemAvailability(id, available);
        return ResponseEntity.noContent().build();
    }

    // ----- Бронирования -----
    @GetMapping("/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationAdminDTO>> getAllReservations() {
        return ResponseEntity.ok(adminService.getAllReservations());
    }

    @GetMapping("/reservations/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservationAdminDTO>> searchReservations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isExtended,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<ReservationAdminDTO> results = adminService.searchReservations(userId, status, isExtended, start, end);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/reservations/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        adminService.cancelReservation(id); // метод должен изменить статус
        return ResponseEntity.noContent().build();
    }


    // Изменить роль пользователя
    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> changeUserRole(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(adminService.changeUserRole(id, userDTO));
    }



    // ----- Заказы -----
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    // ----- Платежи -----
    @GetMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(adminService.getAllPayments());
    }

    // Получить сумму выручки за выбранный период
    @GetMapping("/income")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> getRevenueForPeriod(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        BigDecimal revenue = adminService.getRevenueForPeriod(start, end);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/payments/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentAdminDTO>> getAllPaymentsAdminView() {
        return ResponseEntity.ok(adminService.getAllPaymentsForAdmin());
    }

    @GetMapping("/payments/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentAdminDTO>> searchPayments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(adminService.searchPayments(status, method, username, start, end));
    }


    // ===== Управление комнатами =====
    @GetMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = adminService.getAllRooms()
                .stream()
                .map(room -> convertToDTO(room))
                .toList();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        Room room = adminService.getRoomById(id);
        return ResponseEntity.ok(convertToDTO(room));
    }

    @PostMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomDTO> addRoom(@RequestBody RoomDTO roomDTO) {
        Room createdRoom = adminService.addRoom(roomDTO);
        return ResponseEntity.ok(convertToDTO(createdRoom));
    }

    @PutMapping("/rooms/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Long id, @RequestBody RoomDTO roomDTO) {
        Room updatedRoom = adminService.updateRoom(id, roomDTO);
        return ResponseEntity.ok(convertToDTO(updatedRoom));
    }

    @DeleteMapping("/rooms/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        adminService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Управление столами =====
    @GetMapping("/tables")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TableAdminDTO>> getAllTables() {
        List<TableEntity> tables = adminService.getAllTables();
        List<TableAdminDTO> dtos = tables.stream()
                .map(this::convertToTableDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tables/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TableAdminDTO> getTableById(@PathVariable Long id) {
        TableEntity table = adminService.getTableById(id);
        return ResponseEntity.ok(convertToTableDTO(table));
    }


    @DeleteMapping("/tables/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        adminService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    // Получить все столы в комнате
    @GetMapping("/rooms/{roomId}/tables")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TableAdminDTO>> getTablesByRoomId(@PathVariable Long roomId) {
        List<TableEntity> tables = adminService.getTablesByRoomId(roomId);
        List<TableAdminDTO> dtos = tables.stream()
                .map(this::convertToTableDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping(value = "/tables", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TableAdminDTO> addTableMultipart(
            @RequestParam("label") String label,
            @RequestParam("description") String description,
            @RequestParam("seats") int seats,
            @RequestParam("roomId") Long roomId,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        TableEntity saved = adminService.addTable(label, description, seats, roomId, image);
        return ResponseEntity.ok(convertToTableDTO(saved));
    }

    @PutMapping(value = "/tables/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TableAdminDTO> updateTableMultipart(
            @PathVariable Long id,
            @RequestParam("label") String label,
            @RequestParam("description") String description,
            @RequestParam("seats") int seats,
            @RequestParam("roomId") Long roomId,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        TableEntity updated = adminService.updateTable(id, label, description, seats, roomId, image);
        return ResponseEntity.ok(convertToTableDTO(updated));
    }

    //Сотрудники
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PostMapping("/users")
    public ResponseEntity<Void> addUser(@RequestBody UserDTO userDTO) {
        adminService.createUser(userDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        adminService.updateUser(id, userDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    private TableAdminDTO convertToTableDTO(TableEntity table) {
        TableAdminDTO dto = new TableAdminDTO();
        dto.setId(table.getId());
        dto.setLabel(table.getLabel());
        dto.setDescription(table.getDescription());
        dto.setSeats(table.getSeats());
        dto.setImageUrl(table.getStringUrl()); // предполагается, что StringUrl - это URL картинки
        dto.setRoomId(table.getRoom() != null ? table.getRoom().getId() : null);
        return dto;
    }

    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setRestaurantId(room.getRestaurant() != null ? room.getRestaurant().getId() : null);
        return dto;
    }
}
