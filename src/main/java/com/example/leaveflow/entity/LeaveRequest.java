package com.example.leaveflow.entity;

import jakarta.persistence.*;
import com.example.leaveflow.entity.LeaveStatus;

import java.time.LocalDate;

@Entity
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    public LeaveRequest() {

    }

    public LeaveRequest(LocalDate startDate, LocalDate endDate, String reason, Employee employee){
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.employee = employee;
        this.status = LeaveStatus.PENDING;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee){
        this.employee = employee;
    }

}
