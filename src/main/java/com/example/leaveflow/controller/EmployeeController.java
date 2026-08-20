package com.example.leaveflow.controller;

import java.util.List;

import com.example.leaveflow.dto.PatchEmployeeRequest;
import jakarta.validation.Valid;
import com.example.leaveflow.dto.UpdateEmployeeRequest;
import com.example.leaveflow.dto.EmployeeResponse;
import com.example.leaveflow.dto.CreateEmployeeRequest;
import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public EmployeeResponse createEmployee( @Valid @RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @GetMapping("/api/v1/employees")
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PutMapping("/api/v1/employees/{id}")
    public EmployeeResponse updateEmployee(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateEmployeeRequest request) {

        return employeeService.updateEmployee(id, request);
    }

    @PatchMapping("/api/v1/employees/{id}")
    public EmployeeResponse patchEmployee(
            @PathVariable Integer id,
            @RequestBody PatchEmployeeRequest request) {

        return employeeService.patchEmployee(id, request);
    }

    @DeleteMapping("/api/v1/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
