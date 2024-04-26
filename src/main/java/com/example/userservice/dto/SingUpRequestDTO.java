package com.example.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingUpRequestDTO {
    private String username;
    private String email;
    private String password;
}
