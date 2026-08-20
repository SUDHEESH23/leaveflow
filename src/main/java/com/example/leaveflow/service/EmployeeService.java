package com.example.leaveflow.service;

import java.util.*;
import com.example.leaveflow.dto.PatchEmployeeRequest;
import com.example.leaveflow.dto.UpdateEmployeeRequest;
import com.example.leaveflow.exception.EmployeeNotFoundException;
import com.example.leaveflow.dto.EmployeeResponse;
import com.example.leaveflow.dto.CreateEmployeeRequest;
import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.exception.ValidationException;
import com.example.leaveflow.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        Employee employee = new Employee(
                request.getName(),
                request.getEmail()
        );

        Employee savedEmployee = employeeRepository.save(employee);

        return new EmployeeResponse(
                savedEmployee.getId(),
                savedEmployee.getName(),
                savedEmployee.getEmail()
        );
    }

    public EmployeeResponse getEmployeeById(Integer id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail()
        );
    }

    public List<EmployeeResponse> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(employee -> new EmployeeResponse(
                        employee.getId(),
                        employee.getName(),
                        employee.getEmail()
                ))
                .toList();
    }

    public EmployeeResponse updateEmployee(
            Integer id,
            UpdateEmployeeRequest request
    ) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());

        Employee savedEmployee = employeeRepository.save(employee);

        return new EmployeeResponse(
                savedEmployee.getId(),
                savedEmployee.getName(),
                savedEmployee.getEmail()
        );
    }

    public EmployeeResponse patchEmployee(
            Integer id,
            PatchEmployeeRequest request) {

        Map<String, String> errors = new HashMap<>();

        if (request.getName() != null && request.getName().isBlank()) {
            errors.put("name", "Name must not be blank");
        }

        if (request.getEmail() != null && request.getEmail().isBlank()) {
            errors.put("email", "Email must not be blank");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && !request.getEmail().contains("@")) {
            errors.put("email", "Email must be valid");
        }

        if (request.getName() == null && request.getEmail() == null) {
            errors.put("request", "At least one field must be provided");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        if (request.getName() != null) {
            employee.setName(request.getName());
        }

        if (request.getEmail() != null) {
            employee.setEmail(request.getEmail());
        }

        Employee savedEmployee = employeeRepository.save(employee);

        return new EmployeeResponse(
                savedEmployee.getId(),
                savedEmployee.getName(),
                savedEmployee.getEmail()
        );
    }

    public void deleteEmployee(Integer id) {

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        employeeRepository.delete(employee);
    }
}
