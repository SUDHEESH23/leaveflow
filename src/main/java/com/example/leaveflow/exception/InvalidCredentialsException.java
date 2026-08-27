package com.example.leaveflow.exception;

public class InvalidCredentialsException extends RuntimeException{

    public InvalidCredentialsException() {
        super("Invalid email or password.");
    }
}
