package com.example.leaveflow.controller;

import com.example.leaveflow.dto.CreateLeaveRequest;
import com.example.leaveflow.dto.LeaveRequestResponse;
import com.example.leaveflow.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
