package com.diplom.demo.DTO;

import com.diplom.demo.Enums.UserRole;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private UserRole role;
}
