package com.example.userservice.controller;

import com.example.userservice.dto.*;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public LoginResponseDTO logIn(@RequestBody LoginRequestDTO loginRequestDTO){
        Token token = userService.logIn(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken(token);
        return loginResponseDTO;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> LogOut(@RequestBody  LogOutRequestDTO logOutRequestDTO){
        userService.logOut(logOutRequestDTO.getToken());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    @GetMapping("/validate/{token}")
    public UserDTO validateToken(@PathVariable String token){
        User user = userService.validateToken(token);
       return UserDTO.from(user);
    }

}
