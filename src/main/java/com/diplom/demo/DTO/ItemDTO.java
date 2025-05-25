package com.diplom.demo.DTO;

import java.math.BigDecimal;

public class ItemDTO {
    private Long menuItemId;
    private String name;
    private int quantity;
    private BigDecimal price;

    public ItemDTO(Long menuItemId, String name, int quantity, BigDecimal price) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // геттеры
    public Long getMenuItemId() { return menuItemId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
}

