package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.MenuItemDTO;
import com.diplom.demo.DTO.ReservationDTO;
import com.diplom.demo.DTO.UserDTO;
import com.diplom.demo.DTO.OrderDTO;
import com.diplom.demo.Entity.User;
import com.diplom.demo.Service.CustomUserDetailsService;
import com.diplom.demo.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COOK')")
public class UserController {

    private final UserService userService;

    // Получить информацию о себе
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok(userService.getUserProfile(userDetails));
    }

    // Обновить профиль
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@AuthenticationPrincipal User userDetails, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUserProfile(userDetails, userDTO));
    }

    // Получить меню
    @GetMapping("/menu")
    public ResponseEntity<List<MenuItemDTO>> getMenu() {
        return ResponseEntity.ok(userService.getAllMenuItems());
    }

    // Создать заказ
    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createOrder(@AuthenticationPrincipal User user, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(userService.createOrder(user, orderDTO));
    }

    // Получить свои заказы
    @GetMapping("/orders/mine")
    public ResponseEntity<List<OrderDTO>> getMyOrders(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok(userService.getMyOrders(userDetails));
    }

    // Создать бронирование
    @PostMapping("/reservations")
    public ResponseEntity<ReservationDTO> createReservation(@AuthenticationPrincipal User userDetails, @RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(userService.createReservation(userDetails, reservationDTO));
    }

    // Получить свои бронирования
    @GetMapping("/reservations/mine")
    public ResponseEntity<List<ReservationDTO>> getMyReservations(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok(userService.getMyReservations(userDetails));
    }

    // Отменить бронирование
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(@AuthenticationPrincipal User userDetails, @PathVariable Long id) {
        userService.cancelReservation(userDetails, id);
        return ResponseEntity.noContent().build();
    }
}
