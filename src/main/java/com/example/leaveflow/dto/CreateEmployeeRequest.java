package com.example.leaveflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateEmployeeRequest {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
