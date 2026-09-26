package com.example.leaveflow.controller;

import com.example.leaveflow.dto.CreateLeaveRequest;
import com.example.leaveflow.dto.LeaveRequestResponse;
import com.example.leaveflow.entity.LeaveStatus;
import com.example.leaveflow.exception.ValidationException;
import com.example.leaveflow.security.JwtService;
import com.example.leaveflow.service.LeaveRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaveRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LeaveRequestControllerTest {

    @MockitoBean
    private LeaveRequestService leaveRequestService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldGetLeaveRequestById() throws Exception{

        LeaveRequestResponse response = new LeaveRequestResponse(
                10,
                7,
                LocalDate.of(2026, 11, 29),
                LocalDate.of(2026, 11,30),
                "Vacation",
                LeaveStatus.PENDING
        );
        when(leaveRequestService.getLeaveRequestById(10))
                .thenReturn(response);

        mockMvc.perform(
                MockMvcRequestBuilders.get(
                        "/api/v1/leave-requests/10"
                )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.employeeId").value(7))
                .andExpect(jsonPath("$.reason").value("Vacation"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(leaveRequestService).getLeaveRequestById(10);

    }

    @Test
    void shouldCreateLeaveRequest() throws Exception {

        // Arrange

        LeaveRequestResponse response = new LeaveRequestResponse(
                10,
                7,
                LocalDate.of(2026, 11, 29),
                LocalDate.of(2026, 11, 30),
                "Vacation",
                LeaveStatus.PENDING
        );

        when(leaveRequestService.createLeaveRequest(any(CreateLeaveRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/leave-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                        "startDate": "2026-11-29",
                                        "endDate": "2026-11-30",
                                        "reason": "Vacation"
                                }
                                """
                        )
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.employeeId").value(7))
                .andExpect(jsonPath("$.reason").value("Vacation"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(leaveRequestService)
                .createLeaveRequest(any(CreateLeaveRequest.class));

    }

    @Test
    void shouldRejectInvalidCreateLeaveRequest() throws Exception {

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/leave-requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "startDate": "2026-11-29",
                                "endDate": "2026-11-30"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(leaveRequestService, never())
                .createLeaveRequest(any(CreateLeaveRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {

        // Arrange
        doThrow(new ValidationException(
                Map.of("dates", "Employee already has overlapping leave")
        ))
                .when(leaveRequestService)
                .getLeaveRequestById(10);

        // Act + Assert
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/leave-requests/10")
                )
                .andExpect(status().isBadRequest());

        // Verify
        verify(leaveRequestService)
                .getLeaveRequestById(10);
    }

    @Test
    void shouldGetAllLeaveRequestsWithPagination() throws Exception {

        LeaveRequestResponse response = new LeaveRequestResponse(
                10,
                7,
                LocalDate.of(2026, 11, 29),
                LocalDate.of(2026, 11, 30),
                "Vacation",
                LeaveStatus.PENDING
        );

        Page<LeaveRequestResponse> page = new PageImpl<>(List.of(response),
                PageRequest.of(0, 2),
                1);

        when(leaveRequestService.getAllLeaveRequests(any()))
                .thenReturn(page);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/v1/leave-requests")
                        .param("page","0")
                        .param("size","2")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].employeeId").value(7))
                .andExpect(jsonPath("$.content[0].reason").value("Vacation"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2));

        verify(leaveRequestService)
                .getAllLeaveRequests(any());
    }
}
