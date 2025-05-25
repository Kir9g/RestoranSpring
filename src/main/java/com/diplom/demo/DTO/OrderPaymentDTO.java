package com.diplom.demo.DTO;

import java.math.BigDecimal;
import java.util.List;

public class OrderPaymentDTO {
    private Long id;
    private Long reservationId;
    private BigDecimal totalAmount;
    private List<ItemDTO> items;
    private boolean paid;


    public OrderPaymentDTO(Long id, Long reservationId, List<ItemDTO> items, boolean paid) {
        this.id = id;
        this.reservationId = reservationId;
        this.items = items;
        this.paid = paid;
        this.totalAmount = calculateTotalAmount();
    }

    private BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // геттеры
    public Long getId() { return id; }
    public Long getReservationId() { return reservationId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public List<ItemDTO> getItems() { return items; }

    public boolean isPaid() {
        return paid;
    }
}
