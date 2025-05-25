package com.diplom.demo.Repository;

import com.diplom.demo.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByRestaurantId(Long restaurantId);
}
