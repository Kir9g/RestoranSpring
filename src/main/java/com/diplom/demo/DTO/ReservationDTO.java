package com.diplom.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private Long userId;
    private Long tableId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private boolean extended;
}

