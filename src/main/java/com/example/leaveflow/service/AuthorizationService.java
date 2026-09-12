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

    public AuthorizationService(
            EmployeeRepository employeeRepository,
            UserAccountRepository userAccountRepository
    ){
        this.employeeRepository = employeeRepository;
    }

    public Employee getCurrentEmployee() {
        String currentUserEmail = getCurrentAuthentication().getName();
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

        UserRole role = getCurrentUserRole();

        if (role == UserRole.ADMIN) {
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

    public void verifyManagerCanActOn(LeaveRequest leaveRequest) {

        Employee currentManager = getCurrentEmployee();

        if (leaveRequest.getEmployee().getManager() == null ||
                !leaveRequest.getEmployee().getManager().getId()
                        .equals(currentManager.getId())) {

            throw new ForbiddenException(
                    "You are not allowed to act on this leave request");
        }
    }

    public void verifyCanViewEmployee(Employee employee) {

        UserRole role = getCurrentUserRole();

        if(role == UserRole.ADMIN){
            return;
        }

        Employee currentEmployee = getCurrentEmployee();

        boolean isSelf = employee.getId().equals(currentEmployee.getId());

        boolean isManagerOfEmployee = employee.getManager() != null && employee.getManager().getId().equals(currentEmployee.getId());

        if(!isSelf && !isManagerOfEmployee){
            throw new ForbiddenException("You are not allowed to view this employee.");
        }

    }

    public void verifyAdmin() {

        if (getCurrentUserRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Admin access required");
        }
    }

}
