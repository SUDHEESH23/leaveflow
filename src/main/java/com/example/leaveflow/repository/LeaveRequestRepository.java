package com.example.leaveflow.repository;

import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

    boolean existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Integer employeeId,
            List<LeaveStatus> statuses,
            LocalDate endDate,
            LocalDate startDate
    );

    boolean existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdNot(
            Integer employeeId,
            List<LeaveStatus> statuses,
            LocalDate endDate,
            LocalDate startDate,
            Integer id
    );

    List<LeaveRequest> findByEmployeeId(Integer employeeId);

    List<LeaveRequest> findByEmployeeManagerId(Integer managerId);
}
