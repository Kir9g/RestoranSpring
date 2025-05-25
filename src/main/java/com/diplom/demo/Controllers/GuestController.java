package com.diplom.demo.Controllers;

import com.diplom.demo.DTO.*;
import com.diplom.demo.Service.GuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
@Slf4j
public class GuestController {
    @Autowired
    private GuestService guestService;

    // Получить меню по категории
    @GetMapping("/menu/{category}")
    public ResponseEntity<List<MenuItemDTO>> getMenu(@PathVariable String category) {
        log.info(category);
        List<MenuItemDTO> menu = guestService.getAvailableMenuItemsByCategory(category);
        return ResponseEntity.ok(guestService.getAvailableMenuItemsByCategory(category));
    }

    @GetMapping("/restaurant")
    public RestaurantDTO getRestaurantInfo() {
        return guestService.getRestaurantInfo();
    }

    @GetMapping("/rooms")
    public List<RoomDTO> getAllRooms() {
        return guestService.getAllRooms();
    }

    @GetMapping("/tables")
    public List<TableDTO> getTablesAtTime(@RequestParam String time) {
        LocalTime localTime = LocalTime.parse(time);
        LocalDateTime selectedDateTime = LocalDateTime.of(LocalDate.now(), localTime);
        return guestService.getTablesAtTime(selectedDateTime);
    }


    @GetMapping("/menu")
    public List<MenuItemDTO> getAvailableMenuItems() {
        return guestService.getAllAvailableMenuItems();
    }


}
