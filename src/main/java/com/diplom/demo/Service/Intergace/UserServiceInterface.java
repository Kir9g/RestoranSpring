package com.diplom.demo.Service.Intergace;

import com.diplom.demo.DTO.MenuItemDTO;
import com.diplom.demo.DTO.OrderDTO;
import com.diplom.demo.DTO.ReservationDTO;
import com.diplom.demo.DTO.UserDTO;
import com.diplom.demo.Entity.User;
import com.diplom.demo.Service.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserServiceInterface {
    UserDTO getUserProfile(User user);
    UserDTO updateUserProfile(User user, UserDTO userDTO);
    List<MenuItemDTO> getAllMenuItems();
    OrderDTO createOrder(User user, OrderDTO orderDTO);
    List<OrderDTO> getMyOrders(User user);
    ReservationDTO createReservation(User user, ReservationDTO reservationDTO);
    List<ReservationDTO> getMyReservations(User user);
    void cancelReservation(User user, Long id);
}
