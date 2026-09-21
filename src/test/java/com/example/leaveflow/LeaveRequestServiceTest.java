package com.example.leaveflow;

import com.example.leaveflow.dto.LeaveRequestResponse;
import com.example.leaveflow.dto.UpdateLeaveRequest;
import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.entity.LeaveStatus;
import com.example.leaveflow.exception.ForbiddenException;
import com.example.leaveflow.exception.LeaveRequestNotFoundException;
import com.example.leaveflow.repository.EmployeeRepository;
import com.example.leaveflow.repository.LeaveRequestRepository;
import com.example.leaveflow.service.AuthorizationService;
import com.example.leaveflow.service.LeaveRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private LeaveRequest leaveRequest;

    @Mock
    private Employee employee;

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    @Test
    void shouldGetLeaveRequestById() {

        // Arrange
        when(leaveRequestRepository.findById(10))
                .thenReturn(Optional.of(leaveRequest));

        doNothing()
                .when(authorizationService)
                .verifyCanViewLeave(leaveRequest);

        when(leaveRequest.getEmployee())
                .thenReturn(employee);

        when(employee.getId())
                .thenReturn(7);

        when(leaveRequest.getId())
                .thenReturn(10);

        when(leaveRequest.getStartDate())
                .thenReturn(LocalDate.of(2026, 11, 29));

        when(leaveRequest.getEndDate())
                .thenReturn(LocalDate.of(2026, 11, 30));

        when(leaveRequest.getReason())
                .thenReturn("Vacation");

        when(leaveRequest.getStatus())
                .thenReturn(LeaveStatus.PENDING);

        // Act
        LeaveRequestResponse response =
                leaveRequestService.getLeaveRequestById(10);

        // Assert
        assertEquals(10, response.getId());
        assertEquals(7, response.getEmployeeId());
        assertEquals("Vacation", response.getReason());
        assertEquals(LeaveStatus.PENDING, response.getStatus());

        verify(leaveRequestRepository).findById(10);
        verify(authorizationService).verifyCanViewLeave(leaveRequest);

    }

    @Test
    void shouldRejectUnauthorizedLeaveRequest() {

        // Arrange
        when(leaveRequestRepository.findById(10))
                .thenReturn(Optional.of(leaveRequest));

        doThrow(new ForbiddenException(
                "You are not allowed to view this leave request"))
                .when(authorizationService)
                .verifyCanViewLeave(leaveRequest);

        // Act + Assert
        assertThrows(
                ForbiddenException.class,
                () -> leaveRequestService.getLeaveRequestById(10)
        );
    }

    @Test
    void shouldThrowWhenLeaveRequestDoesNotExist() {

        // Arrange
        when(leaveRequestRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                LeaveRequestNotFoundException.class,
                () -> leaveRequestService.getLeaveRequestById(999)
        );

        // Verify
        verify(leaveRequestRepository).findById(999);
    }

    @Test
    void shouldUpdateLeaveRequest() {

        // Arrange

        UpdateLeaveRequest request = new UpdateLeaveRequest();
        request.setStartDate(LocalDate.of(2026, 11, 29));
        request.setEndDate(LocalDate.of(2026, 11, 30));
        request.setReason("Updated vacation");

        when(leaveRequestRepository.findById(10))
                .thenReturn(Optional.of(leaveRequest));

        when(leaveRequest.getStatus())
                .thenReturn(LeaveStatus.PENDING);

        when(leaveRequest.getEmployee())
                .thenReturn(employee);

        when(employee.getId())
                .thenReturn(7);

        when(leaveRequestRepository
                .existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdNot(
                        eq(7),
                        anyList(),
                        eq(request.getEndDate()),
                        eq(request.getStartDate()),
                        eq(10)
                ))
                .thenReturn(false);

        when(leaveRequestRepository.save(leaveRequest))
                .thenReturn(leaveRequest);

        when(leaveRequest.getId())
                .thenReturn(10);

        when(leaveRequest.getStartDate())
                .thenReturn(request.getStartDate());

        when(leaveRequest.getEndDate())
                .thenReturn(request.getEndDate());

        when(leaveRequest.getReason())
                .thenReturn(request.getReason());

        // Act

        LeaveRequestResponse response =
                leaveRequestService.updateLeaveRequest(10, request);

        // Assert

        assertEquals(10, response.getId());
        assertEquals(7, response.getEmployeeId());
        assertEquals(
                LocalDate.of(2026, 11, 29),
                response.getStartDate()
        );
        assertEquals(
                LocalDate.of(2026, 11, 30),
                response.getEndDate()
        );
        assertEquals("Updated vacation", response.getReason());

        // Verify

        verify(leaveRequestRepository).findById(10);
        verify(authorizationService).verifyEmployeeOwnsLeave(leaveRequest);
        verify(leaveRequestRepository).save(leaveRequest);
    }
}
