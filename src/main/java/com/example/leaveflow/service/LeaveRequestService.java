package com.example.leaveflow.service;

import com.example.leaveflow.dto.CreateLeaveRequest;
import com.example.leaveflow.dto.LeaveRequestResponse;
import com.example.leaveflow.dto.UpdateLeaveRequest;
import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.entity.LeaveStatus;
import com.example.leaveflow.exception.*;
import com.example.leaveflow.repository.EmployeeRepository;
import com.example.leaveflow.repository.LeaveRequestRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final AuthorizationService authorizationService;

    public LeaveRequestService(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository,
            AuthorizationService authorizationService){
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.authorizationService = authorizationService;
    }

    public LeaveRequestResponse createLeaveRequest(CreateLeaveRequest request) {

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException(
                    Map.of("startDate", "Leave cannot start before today")
            );
        }

        if(request.getEndDate().isBefore(request.getStartDate())){
            Map<String, String> errors = new HashMap<>();
            errors.put("endDate","End date must be on or after start date");
            throw new ValidationException(errors);
        }

        Employee employee = authorizationService.getCurrentEmployee();

        List<LeaveStatus> blockingStatuses = List.of(
               LeaveStatus.APPROVED,
               LeaveStatus.PENDING
        );

        boolean overlap = leaveRequestRepository.existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employee.getId(),
                blockingStatuses,
                request.getEndDate(),
                request.getStartDate()
        );

        if(overlap){
            Map<String, String> errors = new HashMap<>();
            errors.put("dates","Employee already has overlapping leave");
            throw new ValidationException(errors);
        }

        LeaveRequest leaveRequest = new LeaveRequest(
                request.getStartDate(),
                request.getEndDate(),
                request.getReason(),
                employee
        );

        LeaveRequest savedRequest =  leaveRequestRepository.save(leaveRequest);

        return new LeaveRequestResponse(
                savedRequest.getId(),
                savedRequest.getEmployee().getId(),
                savedRequest.getStartDate(),
                savedRequest.getEndDate(),
                savedRequest.getReason(),
                savedRequest.getStatus()
                );
    }

    public LeaveRequestResponse getLeaveRequestById(Integer id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException(id));

        authorizationService.verifyCanViewLeave(leaveRequest);

        return new LeaveRequestResponse(
                leaveRequest.getId(),
                leaveRequest.getEmployee().getId(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                leaveRequest.getReason(),
                leaveRequest.getStatus()
        );
    }

    public List<LeaveRequestResponse> getAllLeaveRequests() {
        return leaveRequestRepository.findAll()
                .stream()
                .map(leaveRequest -> new LeaveRequestResponse(
                        leaveRequest.getId(),
                        leaveRequest.getEmployee().getId(),
                        leaveRequest.getStartDate(),
                        leaveRequest.getEndDate(),
                        leaveRequest.getReason(),
                        leaveRequest.getStatus()
                ))
                .toList();
    }

    public LeaveRequestResponse updateLeaveRequest(Integer id, UpdateLeaveRequest request) {

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException(
                    Map.of("startDate", "Leave cannot start before today")
            );
        }

        if(request.getEndDate().isBefore(request.getStartDate())){
            Map<String, String> errors = new HashMap<>();
            errors.put("endDate","End date must be on or after start date");
            throw new ValidationException(errors);
        }

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException(id));

        authorizationService.verifyEmployeeOwnsLeave(leaveRequest);

        if(leaveRequest.getStatus() != LeaveStatus.PENDING){
            Map<String, String> errors = new HashMap<>();
            errors.put("status","Only pending leave requests can be updated");
            throw new ValidationException(errors);
        }

        List<LeaveStatus> blockingStatuses = List.of(
                LeaveStatus.APPROVED,
                LeaveStatus.PENDING
        );

        boolean overlap = leaveRequestRepository.existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdNot(
                leaveRequest.getEmployee().getId(),
                blockingStatuses,
                request.getEndDate(),
                request.getStartDate(),
                id
        );

        if(overlap){
            Map<String, String> errors = new HashMap<>();
            errors.put("dates","Employee already has overlapping leave");
            throw new ValidationException(errors);
        }

        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setReason(request.getReason());

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        return new LeaveRequestResponse(
                savedRequest.getId(),
                savedRequest.getEmployee().getId(),
                savedRequest.getStartDate(),
                savedRequest.getEndDate(),
                savedRequest.getReason(),
                savedRequest.getStatus()
        );

    }

    public LeaveRequestResponse approveLeaveRequest(Integer id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException(id));

        verifyManagerCanActOn(leaveRequest);

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            Map<String, String> errors = new HashMap<>();
            errors.put("status", "Only pending leave requests can be approved");
            throw new ValidationException(errors);
        }

        leaveRequest.setStatus(LeaveStatus.APPROVED);

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        return new LeaveRequestResponse(
                savedRequest.getId(),
                savedRequest.getEmployee().getId(),
                savedRequest.getStartDate(),
                savedRequest.getEndDate(),
                savedRequest.getReason(),
                savedRequest.getStatus()
        );
    }

    public LeaveRequestResponse rejectLeaveRequest(Integer id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException(id));

        verifyManagerCanActOn(leaveRequest);

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            Map<String, String> errors = new HashMap<>();
            errors.put("status", "Only pending leave requests can be rejected");
            throw new ValidationException(errors);
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        return new LeaveRequestResponse(
                savedRequest.getId(),
                savedRequest.getEmployee().getId(),
                savedRequest.getStartDate(),
                savedRequest.getEndDate(),
                savedRequest.getReason(),
                savedRequest.getStatus()
        );
    }

    private void verifyManagerCanActOn(LeaveRequest leaveRequest) {

        String currentUserEmail =
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName();

        Employee currentManager = employeeRepository
                .findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated manager does not have an employee record"));

        if (leaveRequest.getEmployee().getManager() == null ||
                !leaveRequest.getEmployee().getManager().getId()
                        .equals(currentManager.getId())) {

            throw new ForbiddenException(
                    "You are not allowed to act on this leave request");
        }
    }
}
