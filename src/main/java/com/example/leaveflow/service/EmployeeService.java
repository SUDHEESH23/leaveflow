package com.example.leaveflow.service;

import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public String getMessage() {
        return "Hello from Sudheesh!";
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
}
