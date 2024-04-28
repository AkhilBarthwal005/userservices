package com.example.userservice.exception;

public class InvalidPasswordException extends RuntimeException{

    public InvalidPasswordException(String msg){
        super(msg);
    }
}
