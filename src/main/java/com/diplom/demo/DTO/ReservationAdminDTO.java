package com.diplom.demo.DTO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReservationAdminDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isExtended;
    private String status;
    private String userName;
    private String tableName;
}
