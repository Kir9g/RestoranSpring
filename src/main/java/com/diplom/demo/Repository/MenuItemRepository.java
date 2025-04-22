package com.diplom.demo.Repository;

import com.diplom.demo.Entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurantId(Long restaurantId);

    @Modifying
    @Transactional
    @Query("UPDATE MenuItem m SET m.available = :available WHERE m.id = :id")
    void updateAvailabilityById(Long id, boolean available);
}
