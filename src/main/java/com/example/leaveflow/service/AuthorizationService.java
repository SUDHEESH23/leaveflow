package com.example.leaveflow.service;

import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.entity.UserRole;
import com.example.leaveflow.exception.EmployeeAccountNotLinkedException;
import com.example.leaveflow.exception.ForbiddenException;
import com.example.leaveflow.repository.EmployeeRepository;
import com.example.leaveflow.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final EmployeeRepository employeeRepository;
    private final UserAccountRepository userAccountRepository;

    public AuthorizationService(
            EmployeeRepository employeeRepository,
            UserAccountRepository userAccountRepository
    ){
        this.employeeRepository = employeeRepository;
        this.userAccountRepository = userAccountRepository;
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

    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public void verifyCanViewLeave(LeaveRequest leaveRequest) {

        Authentication authentication = getCurrentAuthentication();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority()
                        .equals("ROLE_ADMIN"));
        if(isAdmin){
            return;
        }

        Employee currentEmployee = getCurrentEmployee();

        Employee leaveOwner = leaveRequest.getEmployee();

        boolean isOwner = leaveOwner.getId().equals(currentEmployee.getId());

        boolean isManager = leaveOwner.getManager() != null && leaveOwner.getManager().getId().equals(currentEmployee.getId());

        if(!isOwner && !isManager){
            throw new ForbiddenException(
                    "You are not allowed to view this leave request");
        }
    }

    public UserRole getCurrentUserRole() {
        return getCurrentAuthentication()
                .getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .map(UserRole::valueOf)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user has no valid role"));
    }

}
