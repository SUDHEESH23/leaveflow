package com.example.leaveflow;

import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.entity.LeaveStatus;
import com.example.leaveflow.exception.ForbiddenException;
import com.example.leaveflow.repository.EmployeeRepository;
import com.example.leaveflow.repository.LeaveRequestRepository;
import com.example.leaveflow.service.LeaveRequestService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class LeaveRequestResourceAuthorizationTest {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Test
    @WithMockUser(
            username = "manager-a@test.com",
            roles = "MANAGER"
    )
    void managerCannotApproveLeaveOfAnotherManagersEmployee() {

        // Create Manager A: the logged-in user
        Employee managerA = new Employee();
        managerA.setName("Manager A");
        managerA.setEmail("manager-a@test.com");
        managerA = employeeRepository.save(managerA);

        // Create Manager B
        Employee managerB = new Employee();
        managerB.setName("Manager B");
        managerB.setEmail("manager-b@test.com");
        managerB = employeeRepository.save(managerB);

        // Create an employee reporting to Manager B
        Employee employee = new Employee();
        employee.setName("Employee C");
        employee.setEmail("employee-c@test.com");
        employee.setManager(managerB);
        employee = employeeRepository.save(employee);

        // Create a pending leave request for Employee C
        LeaveRequest leaveRequest = new LeaveRequest(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                "Vacation",
                employee
        );

        LeaveRequest savedLeave =
                leaveRequestRepository.save(leaveRequest);

        // Manager A tries to approve Employee C's leave
        assertThrows(
                ForbiddenException.class,
                () -> leaveRequestService.approveLeaveRequest(
                        savedLeave.getId()
                )
        );

        // Verify that the leave was not approved
        LeaveRequest unchanged = leaveRequestRepository
                .findById(savedLeave.getId())
                .orElseThrow();

        assertEquals(LeaveStatus.PENDING, unchanged.getStatus());
    }
}