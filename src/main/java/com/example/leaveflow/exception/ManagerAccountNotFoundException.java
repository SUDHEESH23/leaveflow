package com.example.leaveflow.exception;

public class ManagerAccountNotFoundException extends RuntimeException {

    public ManagerAccountNotFoundException(String email) {
        super("Manager does not have a user account for email: " + email);
    }
}