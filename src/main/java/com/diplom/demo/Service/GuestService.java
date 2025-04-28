package com.diplom.demo.Service;

import com.diplom.demo.DTO.MenuItemDTO;
import com.diplom.demo.DTO.RestaurantDTO;
import com.diplom.demo.DTO.RoomDTO;
import com.diplom.demo.DTO.TableDTO;
import com.diplom.demo.Entity.MenuItem;
import com.diplom.demo.Entity.Restaurant;
import com.diplom.demo.Entity.Room;
import com.diplom.demo.Entity.TableEntity;
import com.diplom.demo.Repository.MenuItemRepository;
import com.diplom.demo.Repository.RestaurantRepository;
import com.diplom.demo.Repository.RoomRepository;
import com.diplom.demo.Repository.TableEntityRepository;
import com.diplom.demo.Service.Intergace.GuestServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                        item.getCategory(),
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
                        room.getRows(),
                        room.getCols()
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
    public List<TableDTO> getAllTables() {
        List<TableEntity> tables = tableRepository.findAll();
        return tables.stream()
                .map(table -> new TableDTO(
                        table.getId(),
                        table.getLabel(),
                        table.getRowPosition(),
                        table.getColPosition(),
                        table.getSeats(),
                        isTableAvailable(table) // логика для определения доступности
                ))
                .collect(Collectors.toList());
    }

    private boolean isTableAvailable(TableEntity table) {
        return table.getReservations().stream()
                .noneMatch(reservation -> reservation.getStatus().equals("ACTIVE"));
    }

}
