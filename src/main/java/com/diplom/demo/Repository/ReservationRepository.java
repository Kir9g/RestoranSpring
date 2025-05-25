package com.diplom.demo.Repository;

import com.diplom.demo.Entity.Reservation;
import com.diplom.demo.Entity.TableEntity;
import com.diplom.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
    SELECT r FROM Reservation r
    WHERE (COALESCE(:userId, null) IS NULL OR r.user.id = :userId)
      AND (COALESCE(:status, null) IS NULL OR r.status = :status)
      AND (COALESCE(:isExtended, null) IS NULL OR r.isExtended = :isExtended)
      AND (COALESCE(:start, null) IS NULL OR r.startTime >= :start)
      AND (COALESCE(:end, null) IS NULL OR r.endTime <= :end)
    """)
    List<Reservation> searchReservations(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("isExtended") Boolean isExtended,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    Optional<Reservation> findByUser(User user);
    Optional<Reservation> findByIdAndUser(Long id, User user);

    List<Reservation> findByStatus(String status);

    @Query("SELECT r FROM Reservation r WHERE " +
            "r.startTime >= :start AND r.startTime <= :end " +
            "ORDER BY r.startTime")
    List<Reservation> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT r FROM Reservation r WHERE " +
            "r.table.room.restaurant.id = :restaurantId AND " +
            "r.startTime >= :start AND r.startTime <= :end " +
            "ORDER BY r.startTime")
    List<Reservation> findByRestaurantAndDateRange(
            @Param("restaurantId") Long restaurantId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT r FROM Reservation r WHERE " +
            "r.table = :table AND " +
            "r.status = 'ACTIVE' AND " +
            "r.startTime <= :checkTime AND " +
            "r.endTime > :checkTime")
    List<Reservation> findByTableAndTimeRange(
            @Param("table") TableEntity table,
            @Param("checkTime") LocalDateTime checkTime
    );

    @Query("SELECT t FROM TableEntity t WHERE t.room.restaurant.id = :restaurantId")
    List<TableEntity> findByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT r FROM Reservation r WHERE r.user = :user AND r.status = 'ACTIVE' AND r.endTime > :now")
    List<Reservation> findActiveReservationsNow(@Param("user") User user, @Param("now") LocalDateTime now);



    @Query("SELECT r FROM Reservation r WHERE r.user = :user AND (r.endTime < :now OR r.status IN ('COMPLETED', 'CANCELLED'))")
    List<Reservation> findPastReservations(@Param("user") User user, @Param("now") LocalDateTime now);

}


