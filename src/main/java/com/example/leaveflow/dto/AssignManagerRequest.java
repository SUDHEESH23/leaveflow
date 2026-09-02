package com.example.leaveflow.dto;

import jakarta.validation.constraints.NotNull;

public class AssignManagerRequest {

    @NotNull
    private Integer managerId;

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}
