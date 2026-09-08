package com.example.leaveflow.dto;

public class EmployeeResponse {

    private Integer id;
    private String name;
    private String email;
    private Integer managerId;

    public EmployeeResponse(
            Integer id,
            String name,
            String email,
            Integer managerId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.managerId = managerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}