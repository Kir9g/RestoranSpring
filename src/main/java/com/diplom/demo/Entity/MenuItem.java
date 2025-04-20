package com.diplom.demo.Entity;

import com.diplom.demo.Enums.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl; // ссылка на изображение

    @Enumerated(EnumType.STRING)
    private Category category; // например, SOUP, MAIN_DISH, DRINK

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private boolean available = true; // если блюдо временно недоступно
}

