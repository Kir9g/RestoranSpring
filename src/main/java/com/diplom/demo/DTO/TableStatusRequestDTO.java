package com.diplom.demo.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TableStatusRequestDTO {
    private LocalDateTime checkTime; // Время для проверки занятости
}
