package com.example.leaveflow.service;

import com.example.leaveflow.exception.EmployeeNotFoundException;
import com.example.leaveflow.dto.EmployeeResponse;
import com.example.leaveflow.dto.CreateEmployeeRequest;
import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        Employee employee = new Employee(request.getName(), request.getEmail());
        Employee savedEmployee = employeeRepository.save(employee);
        return new EmployeeResponse(savedEmployee.getId(), savedEmployee.getName(), savedEmployee.getEmail());
    }

    public EmployeeResponse getEmployeeById(Integer id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        return new EmployeeResponse(employee.getId(), employee.getName(), employee.getEmail());
    }
}
