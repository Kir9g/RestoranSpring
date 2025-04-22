package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.MenuItemDTO;
import com.diplom.demo.DTO.UserDTO;
import com.diplom.demo.Entity.Order;
import com.diplom.demo.Entity.Payment;
import com.diplom.demo.Entity.Reservation;
import com.diplom.demo.Entity.User;
import com.diplom.demo.Enums.UserRole;
import com.diplom.demo.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Только для админа
public class AdminController {

    @Autowired
    private final AdminService adminService;

    // ----- Меню -----
    //Добавить
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/menu")
    public ResponseEntity<MenuItemDTO> addMenuItem(@RequestBody MenuItemDTO item) {
        return ResponseEntity.ok(adminService.addMenuItem(item));
    }
    //Обновить
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/menu/{id}")
    public ResponseEntity<Optional<MenuItemDTO>> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemDTO item) {
        return ResponseEntity.ok(adminService.updateMenuItem(id, item));
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

    @PutMapping("/menu/{id}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeMenuItemAvailability(@PathVariable Long id, @RequestParam boolean available) {
        adminService.changeMenuItemAvailability(id, available);
        return ResponseEntity.noContent().build();
    }

    // ----- Бронирования -----
    @GetMapping("/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(adminService.getAllReservations());
    }

    @GetMapping("/reservations/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Reservation>> searchReservations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isExtended,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<Reservation> results = adminService.searchReservations(userId, status, isExtended, start, end);
        return ResponseEntity.ok(results);
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
}
