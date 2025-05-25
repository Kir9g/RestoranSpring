package com.diplom.demo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationResponseDTO {
    private Long id;
    private String userName;
    private String userPhone;
    private String tableName;
    private String roomName;
    private String restaurantName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private boolean isExtended;
    private String specialRequests;
}
