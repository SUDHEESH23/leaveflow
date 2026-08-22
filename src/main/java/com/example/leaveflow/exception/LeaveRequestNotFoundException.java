package com.example.leaveflow.exception;

public class LeaveRequestNotFoundException extends RuntimeException {

    public LeaveRequestNotFoundException(Integer id) {
        super("Leave request not found with id: " + id);
    }
}