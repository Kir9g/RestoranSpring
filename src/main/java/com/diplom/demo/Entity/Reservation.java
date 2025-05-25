package com.diplom.demo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isExtended; // продляли или нет

    private String status; // "ACTIVE", "CANCELLED", "COMPLETED", ""

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Order> orders;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableEntity table;

    public void addOrder(Order order) {
        orders.add(order);
        order.setReservation(this);
    }
}

