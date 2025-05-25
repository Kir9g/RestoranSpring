package com.diplom.demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    private Long tableId;
    private List<OrderItemDTO> items;
}