package com.diplom.demo.DTO;

import lombok.Data;

@Data
public class PasswordResetDTO {
    private String email;
    private String code;
    private String newPassword;
}