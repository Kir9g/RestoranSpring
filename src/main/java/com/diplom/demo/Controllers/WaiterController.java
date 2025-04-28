package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.OrderDTO;
import com.diplom.demo.DTO.ReservationDTO;
import com.diplom.demo.Service.WaiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("Waiter")
@PreAuthorize("hasRole('WAITER')")
public class WaiterController {
    @Autowired
    private  WaiterService waiterService;

    @GetMapping("/reservations")
    public List<ReservationDTO> getAllActiveReservations() {
        return waiterService.getAllActiveReservations();
    }

    @PostMapping("/reservations/{id}/confirm")
    public ReservationDTO confirmReservation(@PathVariable Long id) {
        return waiterService.confirmReservation(id);
    }

    @GetMapping("/orders")
    public List<OrderDTO> getAllOrders() {
        return waiterService.getAllOrders();
    }

    @PutMapping("/orders/{id}/status")
    public OrderDTO updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return waiterService.updateOrderStatus(id, status);
    }
}
