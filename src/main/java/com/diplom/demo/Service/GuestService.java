package com.diplom.demo.Service;

import com.diplom.demo.DTO.*;
import com.diplom.demo.Entity.MenuItem;
import com.diplom.demo.Entity.Restaurant;
import com.diplom.demo.Entity.Room;
import com.diplom.demo.Entity.TableEntity;
import com.diplom.demo.Repository.*;
import com.diplom.demo.Service.Intergace.GuestServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestService implements GuestServiceInterface {

    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private TableEntityRepository tableRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<MenuItemDTO> getAllAvailableMenuItems() {
        List<MenuItem> items = menuItemRepository.findByAvailableTrue();
        return items.stream()
                .map(item -> new MenuItemDTO(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getImageUrl(),
                        item.getCategory().getName(),
                        item.getRestaurant() != null ? item.getRestaurant().getId() : null,
                        item.isAvailable()
                ))
                .collect(Collectors.toList());
    }

    public List<MenuItemDTO> getAvailableMenuItemsByCategory(String categoryName) {
        List<MenuItem> items = menuItemRepository.findByCategory_NameIgnoreCaseAndAvailableTrue(categoryName);
        return items.stream()
                .map(item -> new MenuItemDTO(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getImageUrl(),
                        item.getCategory().getName(),
                        item.getRestaurant() != null ? item.getRestaurant().getId() : null,
                        item.isAvailable()
                ))
                .collect(Collectors.toList());
    }
    @Override
    public List<RoomDTO> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(room -> new RoomDTO(
                        room.getId(),
                        room.getName(),
                        room.getRestaurant().getId()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantDTO getRestaurantInfo() {
        Restaurant restaurant = restaurantRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Ресторан не найден"));

        return new RestaurantDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getPhoneNumber(),
                restaurant.getDescription()
        );
    }
    @Override
    public List<TableDTO> getTablesAtTime(LocalDateTime selectedTime) {
        List<TableEntity> tables = tableRepository.findAll();
        return tables.stream()
                .map(table -> new TableDTO(
                        table.getId(),
                        table.getLabel(),
                        table.getDescription(),
                        table.getStringUrl(),
                        table.getSeats(),
                        isTableAvailableAtTime(table, selectedTime)
                ))
                .collect(Collectors.toList());
    }

    private boolean isTableAvailableAtTime(TableEntity table, LocalDateTime selectedTime) {
        boolean hasActiveReservation = table.getReservations().stream()
                .anyMatch(reservation ->
                        reservation.getStatus().equalsIgnoreCase("ACTIVE") &&
                                !reservation.getEndTime().isBefore(selectedTime) &&
                                !reservation.getStartTime().isAfter(selectedTime)
                );
        return !hasActiveReservation && !table.isManuallyOccupied();
    }



}
