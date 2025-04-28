package com.diplom.demo.Service.Intergace;

import com.diplom.demo.DTO.OrderDTO;
import com.diplom.demo.DTO.ReservationDTO;

import java.util.List;

public interface WaiterServiceInterface {
    List<ReservationDTO> getAllActiveReservations(); // получить активные бронирования
    ReservationDTO confirmReservation(Long reservationId); // подтвердить бронирование
    List<OrderDTO> getAllOrders(); // все заказы
    OrderDTO updateOrderStatus(Long orderId, String status); // обновить статус заказа (выдан)
}
}
