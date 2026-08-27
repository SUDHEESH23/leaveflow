package com.example.leaveflow.exception;

public class UserAccountAlreadyExistsException extends RuntimeException {
    public UserAccountAlreadyExistsException(String email) {
        super("User account already exists with email:" + email);
    }
}
