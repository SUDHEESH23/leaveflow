package com.example.leaveflow.dto;

import com.example.leaveflow.entity.UserRole;
import org.apache.catalina.User;

public class RegisterResponse {

    private Integer id;
    private String email;
    private UserRole role;

    public RegisterResponse(Integer id, String email, UserRole role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    private Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }
}
