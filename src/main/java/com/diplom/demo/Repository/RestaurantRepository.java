package com.diplom.demo.Repository;

import com.diplom.demo.Entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findFirstByOrderByIdAsc();

}
