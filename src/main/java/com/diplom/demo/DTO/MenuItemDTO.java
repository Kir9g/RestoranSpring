package com.diplom.demo.DTO;

import com.diplom.demo.Entity.Restaurant;
import com.diplom.demo.Enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
public class MenuItemDTO {
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl; // ссылка на изображение

    @Enumerated(EnumType.STRING)
    private Category category; // например, SOUP, MAIN_DISH, DRINK

    private Long restaurant;

    private boolean available = true;

}
