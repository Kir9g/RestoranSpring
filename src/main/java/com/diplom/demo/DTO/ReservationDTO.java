package com.diplom.demo.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class ReservationDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long tableId;
    private String tableLabel;
    private String roomName;
    private boolean canBeCancelled; // вычисляемое поле
}
