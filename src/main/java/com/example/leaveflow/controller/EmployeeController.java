package com.example.leaveflow.controller;

import com.example.leaveflow.dto.EmployeeResponse;
import com.example.leaveflow.dto.CreateEmployeeRequest;
import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.service.EmployeeService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/api/v1/employees/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping("/api/v1/employees")
    public EmployeeResponse createEmployee(@RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }
}
