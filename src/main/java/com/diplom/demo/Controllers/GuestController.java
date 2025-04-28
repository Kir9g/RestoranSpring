package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.MenuItemDTO;
import com.diplom.demo.DTO.RestaurantDTO;
import com.diplom.demo.DTO.RoomDTO;
import com.diplom.demo.DTO.TableDTO;
import com.diplom.demo.Service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class GuestController {
    @Autowired
    private GuestService guestService;

    @GetMapping("/restaurant")
    public RestaurantDTO getRestaurantInfo() {
        return guestService.getRestaurantInfo();
    }

    @GetMapping("/rooms")
    public List<RoomDTO> getAllRooms() {
        return guestService.getAllRooms();
    }

    @GetMapping("/tables")
    public List<TableDTO> getAllTables() {
        return guestService.getAllTables();
    }

    @GetMapping("/menu")
    public List<MenuItemDTO> getAvailableMenuItems() {
        return guestService.getAllAvailableMenuItems();
    }

}
