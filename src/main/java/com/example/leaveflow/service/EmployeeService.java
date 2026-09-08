package com.example.leaveflow.service;

import java.util.*;

import com.example.leaveflow.dto.*;
import com.example.leaveflow.entity.UserAccount;
import com.example.leaveflow.entity.UserRole;
import com.example.leaveflow.exception.EmployeeNotFoundException;
import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.exception.ManagerAccountNotFoundException;
import com.example.leaveflow.exception.ValidationException;
import com.example.leaveflow.repository.EmployeeRepository;
import com.example.leaveflow.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserAccountRepository userAccountRepository;

    public EmployeeService(EmployeeRepository employeeRepository, UserAccountRepository userAccountRepository) {
        this.employeeRepository = employeeRepository;
        this.userAccountRepository = userAccountRepository;
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
                savedEmployee.getEmail(),
                employee.getManager() != null
                        ? employee.getManager().getId()
                        : null
        );
    }

    public EmployeeResponse getEmployeeById(Integer id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getManager() != null
                        ? employee.getManager().getId()
                        : null
        );
    }

    public List<EmployeeResponse> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(employee -> new EmployeeResponse(
                        employee.getId(),
                        employee.getName(),
                        employee.getEmail(),
                        employee.getManager() != null
                                ? employee.getManager().getId()
                                : null
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
                savedEmployee.getEmail(),
                employee.getManager() != null
                        ? employee.getManager().getId()
                        : null
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
                savedEmployee.getEmail(),
                employee.getManager() != null
                        ? employee.getManager().getId()
                        : null
        );
    }

    public void deleteEmployee(Integer id) {

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        employeeRepository.delete(employee);
    }

    public EmployeeResponse assignManager(
            Integer employeeId,
            AssignManagerRequest request) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(employeeId));

        Employee manager = employeeRepository.findById(request.getManagerId())
                .orElseThrow(() ->
                        new EmployeeNotFoundException(request.getManagerId()));

        UserAccount managerAccount = userAccountRepository
                .findByEmail(manager.getEmail())
                .orElseThrow(() ->
                        new ManagerAccountNotFoundException(manager.getEmail()));

        if (managerAccount.getRole() != UserRole.MANAGER) {
            throw new ValidationException(
                    Map.of("managerId", "Selected employee is not a manager"));
        }

        if (employee.getId().equals(manager.getId())) {
            throw new ValidationException(
                    Map.of("managerId", "An employee cannot be their own manager")
            );
        }

        employee.setManager(manager);

        Employee savedEmployee = employeeRepository.save(employee);

        return new EmployeeResponse(
                savedEmployee.getId(),
                savedEmployee.getName(),
                savedEmployee.getEmail(),
                employee.getManager() != null
                        ? employee.getManager().getId()
                        : null
        );
    }
}
