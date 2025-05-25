package com.diplom.demo.Filters;

import com.diplom.demo.Entity.Reservation;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ReservationSpecifications {
    public static Specification<Reservation> betweenDates(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get("startTime"), start),
                cb.lessThanOrEqualTo(root.get("endTime"), end)
        );
    }

    public static Specification<Reservation> byRestaurant(Long restaurantId) {
        return (root, query, cb) -> cb.equal(
                root.get("table").get("room").get("restaurant").get("id"),
                restaurantId
        );
    }

    public static Specification<Reservation> byStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Reservation> notCancelled() {
        return (root, query, cb) -> cb.notEqual(root.get("status"), "CANCELLED");
    }
}
