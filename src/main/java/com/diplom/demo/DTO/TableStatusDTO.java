package com.diplom.demo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TableStatusDTO {
    private Long tableId;
    private String tableName;
    private int capacity;
    private String roomName;
    private boolean isOccupied;
    private LocalDateTime occupiedUntil; // если занят, до какого времени
}