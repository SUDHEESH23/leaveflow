package com.example.leaveflow.service;

import com.example.leaveflow.dto.CreateLeaveRequest;
import com.example.leaveflow.dto.LeaveRequestResponse;
import com.example.leaveflow.dto.UpdateLeaveRequest;
import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.entity.LeaveStatus;
import com.example.leaveflow.entity.UserRole;
import com.example.leaveflow.exception.*;
import com.example.leaveflow.repository.EmployeeRepository;
import com.example.leaveflow.repository.LeaveRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional
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

    public Page<LeaveRequestResponse> getAllLeaveRequests(Pageable pageable) {

        UserRole role = authorizationService.getCurrentUserRole();

        Page<LeaveRequest> leaveRequests;

        switch (role){
            case ADMIN:
                leaveRequests = leaveRequestRepository.findAll(pageable);
                break;
            case MANAGER:
                Employee manager = authorizationService.getCurrentEmployee();

                leaveRequests = leaveRequestRepository.findByEmployeeIdOrEmployeeManagerId(
                        manager.getId(),
                        manager.getId(),
                        pageable
                );

                break;
            case EMPLOYEE:
                Employee employee = authorizationService.getCurrentEmployee();
                leaveRequests = leaveRequestRepository
                        .findByEmployeeId(employee.getId(), pageable);
                break;
            default:
                throw new IllegalStateException("Unsupported user role.");

        }
        return leaveRequests
                .map(leaveRequest -> new LeaveRequestResponse(
                        leaveRequest.getId(),
                        leaveRequest.getEmployee().getId(),
                        leaveRequest.getStartDate(),
                        leaveRequest.getEndDate(),
                        leaveRequest.getReason(),
                        leaveRequest.getStatus()
                ));
    }

    @Transactional
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

    @Transactional
    public LeaveRequestResponse approveLeaveRequest(Integer id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException(id));

        authorizationService.verifyManagerCanActOn(leaveRequest);

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

    @Transactional
    public LeaveRequestResponse rejectLeaveRequest(Integer id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException(id));

        authorizationService.verifyManagerCanActOn(leaveRequest);

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

    @Transactional
    public LeaveRequestResponse cancelLeaveRequest(Integer id) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException(id));

        authorizationService.verifyEmployeeOwnsLeave(leaveRequest);

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new ValidationException(
                    Map.of(
                            "status",
                            "Only pending leave requests can be cancelled"
                    )
            );
        }

        leaveRequest.setStatus(LeaveStatus.CANCELLED);

        LeaveRequest savedRequest =
                leaveRequestRepository.save(leaveRequest);

        return new LeaveRequestResponse(
                savedRequest.getId(),
                savedRequest.getEmployee().getId(),
                savedRequest.getStartDate(),
                savedRequest.getEndDate(),
                savedRequest.getReason(),
                savedRequest.getStatus()
        );
    }
}
