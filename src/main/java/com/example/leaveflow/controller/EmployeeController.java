package com.example.leaveflow.controller;

import java.util.List;

import com.example.leaveflow.dto.*;
import jakarta.validation.Valid;
import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.service.EmployeeService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping("/employees")
    public EmployeeResponse createEmployee( @Valid @RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @GetMapping("/employees")
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PutMapping("/employees/{id}")
    public EmployeeResponse updateEmployee(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateEmployeeRequest request) {

        return employeeService.updateEmployee(id, request);
    }

    @PatchMapping("/employees/{id}")
    public EmployeeResponse patchEmployee(
            @PathVariable Integer id,
            @RequestBody PatchEmployeeRequest request) {

        return employeeService.patchEmployee(id, request);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/employees/{employeeId}/manager")
    public EmployeeResponse assignManager(
            @PathVariable Integer employeeId,
            @Valid @RequestBody AssignManagerRequest request) {

        return employeeService.assignManager(employeeId, request);
    }
}
