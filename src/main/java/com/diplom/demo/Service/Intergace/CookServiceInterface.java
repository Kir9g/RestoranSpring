package com.diplom.demo.Service.Intergace;

import com.diplom.demo.DTO.OrderDTO;

import java.util.List;

public interface CookServiceInterface {
    List<OrderDTO> getActiveOrders();   // Новые + В работе
    void acceptOrder(Long orderId);
    void completeOrder(Long orderId);
}

