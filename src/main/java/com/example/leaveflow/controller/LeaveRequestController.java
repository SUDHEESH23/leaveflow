package com.example.leaveflow.controller;

import com.example.leaveflow.dto.CreateLeaveRequest;
import com.example.leaveflow.dto.LeaveRequestResponse;
import com.example.leaveflow.dto.UpdateLeaveRequest;
import com.example.leaveflow.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<LeaveRequestResponse> getAllLeaveRequests(Pageable pageable) {
        return leaveRequestService.getAllLeaveRequests(pageable);
    }

    @PutMapping("/{id}")
    public LeaveRequestResponse updateLeaveRequest(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateLeaveRequest request) {

        return leaveRequestService.updateLeaveRequest(id, request);
    }

    @PostMapping("/{id}/approve")
    public LeaveRequestResponse approveLeaveRequest(
            @PathVariable Integer id) {

        return leaveRequestService.approveLeaveRequest(id);
    }

    @PostMapping("/{id}/reject")
    public LeaveRequestResponse rejectLeaveRequest(
            @PathVariable Integer id) {

        return leaveRequestService.rejectLeaveRequest(id);
    }

    @PostMapping("{id}/cancel")
    public LeaveRequestResponse cancelLeaveRequest(
            @PathVariable Integer id
    ){
        return leaveRequestService.cancelLeaveRequest(id);
    }

}
