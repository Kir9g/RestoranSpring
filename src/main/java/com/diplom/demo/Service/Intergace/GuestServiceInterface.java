package com.diplom.demo.Service.Intergace;

import com.diplom.demo.DTO.MenuItemDTO;
import com.diplom.demo.DTO.RestaurantDTO;
import com.diplom.demo.DTO.RoomDTO;
import com.diplom.demo.DTO.TableDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface GuestServiceInterface {
    List<MenuItemDTO> getAllAvailableMenuItems();
    List<RoomDTO> getAllRooms();
    RestaurantDTO getRestaurantInfo();
    List<TableDTO> getTablesAtTime(LocalDateTime selectedTime);
}
