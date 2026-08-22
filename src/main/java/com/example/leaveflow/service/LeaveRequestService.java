package com.example.leaveflow.service;

import com.example.leaveflow.dto.CreateLeaveRequest;
import com.example.leaveflow.dto.LeaveRequestResponse;
import com.example.leaveflow.entity.Employee;
import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.entity.LeaveStatus;
import com.example.leaveflow.exception.EmployeeNotFoundException;
import com.example.leaveflow.exception.LeaveRequestNotFoundException;
import com.example.leaveflow.exception.ValidationException;
import com.example.leaveflow.repository.EmployeeRepository;
import com.example.leaveflow.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveRequestService(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository){
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
    }

    public LeaveRequestResponse createLeaveRequest(CreateLeaveRequest request) {

        if(request.getEndDate().isBefore(request.getStartDate())){
            Map<String, String> errors = new HashMap<>();
            errors.put("endDate","End date must be on or after start date");
            throw new ValidationException(errors);
        }

        List<LeaveStatus> blockingStatuses = List.of(
               LeaveStatus.APPROVED,
               LeaveStatus.PENDING
        );

        boolean overlap = leaveRequestRepository.existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                request.getEmployeeId(),
                blockingStatuses,
                request.getStartDate(),
                request.getEndDate()
        );

        if(overlap){
            Map<String, String> errors = new HashMap<>();
            errors.put("dates","Employee already has overlapping leave");
            throw new ValidationException(errors);
        }

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));

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
}
