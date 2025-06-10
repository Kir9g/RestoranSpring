package com.diplom.demo.DTO;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReservationCodeDTO {
    private Long tableid;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Nullable
    private String code;
}
