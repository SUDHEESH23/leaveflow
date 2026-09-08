package com.example.leaveflow.exception;

public class EmployeeAccountNotLinkedException extends RuntimeException {

    public EmployeeAccountNotLinkedException(String email) {
        super("Authenticated user does not have an employee record for email: " + email);
    }
}