package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.OrderDTO;
import com.diplom.demo.Service.CookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cook")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COOK')")
public class CookController {

    private final CookService cookService;

    @GetMapping("/orders/active")
    public List<OrderDTO> getActiveOrders() {
        return cookService.getActiveOrders();
    }

    @PostMapping("/orders/{id}/accept")
    public void acceptOrder(@PathVariable Long id) {
        cookService.acceptOrder(id);
    }

    @PostMapping("/orders/{id}/complete")
    public void completeOrder(@PathVariable Long id) {
        cookService.completeOrder(id);
    }
}

