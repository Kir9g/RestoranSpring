package com.diplom.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String imageUrl;
}

