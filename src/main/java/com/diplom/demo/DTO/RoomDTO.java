package com.diplom.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private Long id;
    private String name;
    private Long restaurantId; // ID ресторана, к которому относится комната
}

