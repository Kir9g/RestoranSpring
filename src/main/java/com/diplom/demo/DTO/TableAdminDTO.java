package com.diplom.demo.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TableAdminDTO {
    private Long id;
    private String label; // Например, "T1", "VIP-1"
    private String description;
    private String imageUrl; // Ссылка на изображение стола (если нужно)
    private int seats;
    private Long roomId; // ID комнаты, в которой находится стол
}
