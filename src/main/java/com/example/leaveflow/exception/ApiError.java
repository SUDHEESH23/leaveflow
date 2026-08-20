package com.example.leaveflow.exception;

import java.util.Map;

public class ApiError {

    private int status;
    private String message;
    private Map<String, String> errors;

    public ApiError(int status, String message, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
