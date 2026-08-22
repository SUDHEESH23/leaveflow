package com.example.leaveflow.controller;

import com.example.leaveflow.dto.CreateLeaveRequest;
import com.example.leaveflow.dto.LeaveRequestResponse;
import com.example.leaveflow.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService){
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping
    public LeaveRequestResponse createLeaveRequest(@Valid @RequestBody CreateLeaveRequest request) {
        return leaveRequestService.createLeaveRequest(request);
    }

    @GetMapping("/{id}")
    public LeaveRequestResponse getLeaveRequestById(@PathVariable Integer id) {
        return leaveRequestService.getLeaveRequestById(id);
    }

    @GetMapping
    public List<LeaveRequestResponse> getAllLeaveRequests() {
        return leaveRequestService.getAllLeaveRequests();
    }

}
