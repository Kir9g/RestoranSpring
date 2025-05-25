package com.diplom.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTOLIST {
    private Long id;
    private Long userId;
    private Long tableId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String LabelName;
    private String RoomName;
    private String status;
    private List<Long> orderIds;
    private boolean extended;
    private boolean canBeCancelled; // вычисляемое поле
}

