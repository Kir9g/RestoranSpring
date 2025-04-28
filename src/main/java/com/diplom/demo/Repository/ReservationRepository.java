package com.diplom.demo.Repository;

import com.diplom.demo.Entity.Reservation;
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
}


