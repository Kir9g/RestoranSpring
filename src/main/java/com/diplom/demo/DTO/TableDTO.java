package com.diplom.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableDTO {
    private Long id;
    private String label;
    private int rowPosition;
    private int colPosition;
    private int seats;
    private boolean available; // свободен ли столик
}
