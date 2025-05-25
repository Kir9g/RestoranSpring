package com.diplom.demo.Repository;

import com.diplom.demo.Entity.Order;
import com.diplom.demo.Entity.User;
import com.diplom.demo.Enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUser(User user);

    List<Order> findByStatus(OrderStatus status);

    @Query("""
        SELECT o FROM Order o 
        WHERE o.status IN :statuses 
          AND o.createdAt BETWEEN :start AND :end
          AND (
              o.reservation IS NULL 
              OR o.reservation.startTime <= :allowedTime
          )
    """)
    List<Order> findActiveOrdersWithinTime(
            List<OrderStatus> statuses,
            LocalDateTime start,
            LocalDateTime end,
            LocalDateTime allowedTime);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.reservation.startTime BETWEEN :from AND :to")
    List<Order> findOrdersWithReservationStartBetweenAndStatus(LocalDateTime from, LocalDateTime to, OrderStatus status);

}
