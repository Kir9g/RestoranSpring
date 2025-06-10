package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.*;
import com.diplom.demo.Entity.User;
import com.diplom.demo.Enums.Category;
import com.diplom.demo.Service.ResetCodeData;
import com.diplom.demo.Service.UserService;
import com.diplom.demo.Service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class UserController {
    @Autowired
    private  UserService userService;
    @Autowired
    private VerificationCodeService verificationCodeService;
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
    public ResponseEntity<?> createReservation(@AuthenticationPrincipal User userDetails
            , @RequestBody ReservationCodeDTO reservationDTO) {
        verificationCodeService.verifyAndActivateReservation(userDetails, reservationDTO);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/reservations/send-code")
    public ResponseEntity<?> sendVerificationCode(@AuthenticationPrincipal User user,
                                                  @RequestBody ReservationCodeDTO dto ) {
        verificationCodeService.generateAndSendCode(user,dto);
        return ResponseEntity.ok().build();
    }


    // Получить свои бронирования
    @GetMapping("/reservations/mine")
    public ResponseEntity<List<ReservationDTO>> getMyReservations(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok(userService.getMyReservations(userDetails));
    }

    // Получить активные бронирования
    @GetMapping("/reservations/active")
    public ResponseEntity<List<ReservationDTOLIST>> getMyActiveReservations(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok(userService.getMyActiveReservations(userDetails));
    }

    // Получить прошедшие бронирования
    @GetMapping("/reservations/past")
    public ResponseEntity<List<ReservationDTOLIST>> getMyPastReservations(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok(userService.getMyPastReservations(userDetails));
    }

    // Отменить бронирование
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(@AuthenticationPrincipal User userDetails, @PathVariable Long id) {
        userService.cancelReservation(userDetails, id);
        return ResponseEntity.noContent().build();
    }

    // Список всех столов с бронированием по времени
    @PostMapping("/statusOfTables")
    public ResponseEntity<List<TableStatusDTO>> getTablesStatus(
            @RequestBody TableStatusRequestDTO request
    ) {
        return ResponseEntity.ok(userService.getAllTablesWithStatus(request));
    }

    //оплата
    @PostMapping("/orders/{orderId}/pay")
    public ResponseEntity<PaymentDTO> payForOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "ONLINE") String method
    ) {

        return ResponseEntity.ok(userService.mockPay(user, orderId, method));
    }
    //Заказ по id
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderPaymentDTO> getOrderForPayment(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(userService.getOrderPaymentDTO(user, orderId));
    }
}
