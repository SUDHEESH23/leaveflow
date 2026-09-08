package com.example.leaveflow.service;

import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.exception.EmployeeAccountNotLinkedException;
import com.example.leaveflow.exception.ForbiddenException;
import com.example.leaveflow.repository.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final EmployeeRepository employeeRepository;

    public AuthorizationService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public Employee getCurrentEmployee() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeRepository.findByEmail(currentUserEmail).orElseThrow(
                () -> new EmployeeAccountNotLinkedException(currentUserEmail)
        );
    }

    public void verifyEmployeeOwnsLeave(LeaveRequest leaveRequest) {

        Employee currentEmployee = getCurrentEmployee();

        if (!leaveRequest.getEmployee().getId()
                .equals(currentEmployee.getId())) {

            throw new ForbiddenException(
                    "You are not allowed to access this leave request");
        }
    }

}
