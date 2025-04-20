package com.diplom.demo.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthRequstDTO {
    private String username;
    private String password;
}