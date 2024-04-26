package com.example.userservice.controller;

import com.example.userservice.dto.LogOutRequestDTO;
import com.example.userservice.dto.LoginRequestDTO;
import com.example.userservice.dto.SingUpRequestDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserDTO signUp(@RequestBody SingUpRequestDTO singUpRequestDTO){
        User user = userService.signUp(singUpRequestDTO.getEmail(), singUpRequestDTO.getPassword(), singUpRequestDTO.getUsername());
        return UserDTO.from(user);
    }

    @PostMapping("/login")
    public Token logIn(LoginRequestDTO loginRequestDTO){
        return  null;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> LogOut(LogOutRequestDTO logOutRequestDTO){
        return  null;
    }
}
