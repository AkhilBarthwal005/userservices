package com.example.userservice.dto;

import com.example.userservice.models.Role;
import com.example.userservice.models.User;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class UserDTO {
    private String username;
    private String email;
    @ManyToOne
    private List<Role> roles;
    private boolean isEmailVerified;

    public static UserDTO from(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setRoles(user.getRoles());
        userDTO.setEmailVerified(user.isEmailVerified());

        return userDTO;
    }
}
