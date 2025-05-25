package com.diplom.demo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class TableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String label; // например, "T1", "T2"
    private String description;
    private String StringUrl;
    private int seats;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;


    @OneToMany(mappedBy = "table")
    private List<Reservation> reservations;
}

