package com.example.leaveflow;

import com.example.leaveflow.exception.LeaveRequestNotFoundException;
import com.example.leaveflow.service.LeaveRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class LeaveRequestMethodSecurityTest {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Test
    @WithMockUser(roles = "MANAGER")
    void employeeCannotApproveLeave() {

        assertThrows(
                LeaveRequestNotFoundException.class,
                () -> leaveRequestService.approveLeaveRequest(9999)
        );
    }
}
