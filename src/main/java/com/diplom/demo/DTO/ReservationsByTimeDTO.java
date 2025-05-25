package com.diplom.demo.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationsByTimeDTO {
    private LocalDateTime timeSlot; // Временной слот (например, начало часа)
    private List<ReservationDTO> reservations;
    private int totalReservations;
}
