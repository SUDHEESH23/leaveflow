package com.example.leaveflow;

import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.repository.EmployeeRepository;
import com.example.leaveflow.repository.LeaveRequestRepository;
import com.example.leaveflow.service.AuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LeaveRequestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @MockitoBean
    private AuthorizationService authorizationService;

    @Test
    void shouldCreateLeaveRequestEndToEnd() throws Exception {

        Employee employee = new Employee();
        employee.setName("Test Employee");
        employee.setEmail("testemployee@test.com");

        employee = employeeRepository.save(employee);

        when(authorizationService.getCurrentEmployee())
                .thenReturn(employee);

        mockMvc.perform(
                        post("/api/v1/leave-requests")
                                .contentType("application/json")
                                .content("""
                                {
                                    "startDate": "2026-11-01",
                                    "endDate": "2026-11-03",
                                    "reason": "Vacation"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(employee.getId()))
                .andExpect(jsonPath("$.reason").value("Vacation"));

        Page<LeaveRequest> savedLeaves =
                leaveRequestRepository.findByEmployeeId(
                        employee.getId(),
                        PageRequest.of(0, 10)
                );

        assertEquals(1, savedLeaves.getTotalElements());
    }

    @Test
    void shouldRejectOverlappingLeaveRequest() throws Exception {

        Employee employee = new Employee();
        employee.setName("Overlap Employee");
        employee.setEmail("overlap@test.com");

        employee = employeeRepository.save(employee);

        LeaveRequest existingLeave = new LeaveRequest(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12),
                "Existing leave",
                employee
        );

        leaveRequestRepository.save(existingLeave);

        when(authorizationService.getCurrentEmployee())
                .thenReturn(employee);

        mockMvc.perform(
                        post("/api/v1/leave-requests")
                                .contentType("application/json")
                                .content("""
                            {
                                "startDate": "2026-11-11",
                                "endDate": "2026-11-15",
                                "reason": "New Vacation"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        Page<LeaveRequest> savedLeaves =
                leaveRequestRepository.findByEmployeeId(
                        employee.getId(),
                        PageRequest.of(0, 10)
                );

        assertEquals(1, savedLeaves.getTotalElements());
    }

    @Test
//    @WithMockUser(username = "employee@test.com", roles = "EMPLOYEE")
    void employeeShouldNotApproveLeaveRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/leave-requests/10/approve")
                )
                .andExpect(status().isForbidden());
    }

    @Test
//    @WithMockUser(username = "manager@test.com", roles = "MANAGER")
    void managerShouldBeAllowedToApproveLeaveRequest() throws Exception {

        mockMvc.perform(
                        post("/api/v1/leave-requests/99999/approve")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void unauthenticatedUserShouldNotAccessLeaveRequests() throws Exception {

        mockMvc.perform(
                        get("/api/v1/leave-requests")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidJwtShouldNotAccessProtectedEndpoint() throws Exception {

        mockMvc.perform(
                        get("/api/v1/leave-requests")
                                .header("Authorization", "Bearer invalid-token")
                )
                .andExpect(status().isForbidden());
    }
}