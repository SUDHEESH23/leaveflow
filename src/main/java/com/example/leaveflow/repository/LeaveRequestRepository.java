package com.example.leaveflow.repository;

import com.example.leaveflow.entity.LeaveRequest;
import com.example.leaveflow.entity.LeaveStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    Page<LeaveRequest> findByEmployeeId(Integer employeeId,
                                        Pageable pageable  );

    Page<LeaveRequest> findByEmployeeIdOrEmployeeManagerId(
            Integer employeeId,
            Integer managerId,
            Pageable pageable
    );

    List<LeaveRequest> findByEmployeeManagerId(Integer managerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.id = :id")
    Optional<LeaveRequest> findByIdForUpdate(
            @Param("id") Integer id
    );

//    @Query("""
//        SELECT lr
//        FROM LeaveRequest lr
//        JOIN FETCH lr.employee
//    """)
//    List<LeaveRequest> findAllWithEmployee();
}
