package com.example.leaveflow.dto;

import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.entity.LeaveStatus;

import java.time.LocalDate;

public class LeaveRequestResponse {

    private Integer id;
    private Integer employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;

    public LeaveRequestResponse(Integer id, Integer employeeId, LocalDate startDate, LocalDate endDate, String reason, LeaveStatus status){
        this.id = id;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }
}
