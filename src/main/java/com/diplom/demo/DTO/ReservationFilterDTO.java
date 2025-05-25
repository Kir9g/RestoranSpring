package com.diplom.demo.DTO;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ReservationFilterDTO {
    private LocalDate date;
    private Long restaurantId;
    private String status;
    private String grouping; // hour, day
    private String sortBy; // time, user, table
    private String sortDirection; // asc, desc
    private boolean includePast;
    private boolean includeCancelled;
}
